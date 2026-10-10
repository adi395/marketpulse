package com.marketpulse.backend.resilience;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/**
 * A small reliability wrapper around a value that is expensive or rate-limited
 * to fetch (here, responses from external market-data APIs). For each key it
 * provides four things:
 *
 * <ul>
 *   <li><b>Freshness TTL</b> — a successful value is cached for {@code ttl} so
 *       we do not hammer the provider.</li>
 *   <li><b>Request de-duplication</b> — Caffeine's {@code get(key, loader)}
 *       runs the loader at most once per key even under concurrent callers;
 *       the others wait for that single result (cache-stampede protection).</li>
 *   <li><b>Bounded retry</b> — the loader is retried up to {@code maxAttempts}
 *       times for transient failures. Set to 1 for quota-bound providers where
 *       retrying would simply waste the daily budget.</li>
 *   <li><b>Last-good stale fallback</b> — if a fresh load fails, the most
 *       recent successful value is returned flagged {@code stale} instead of
 *       propagating the error. Only if nothing good was ever cached does the
 *       error surface.</li>
 * </ul>
 *
 * This is intentionally in-process: no external cache/broker is needed for a
 * single-instance service, which keeps the infrastructure minimal.
 */
public class ResilientLoader<T> {
    private static final Logger log = LoggerFactory.getLogger(ResilientLoader.class);

    /** A value plus when it was fetched and whether it is being served stale. */
    public record Result<T>(T value, Instant asOf, boolean stale) {}

    private final String name;
    private final int maxAttempts;
    private final Cache<String, Result<T>> fresh;
    private final ConcurrentHashMap<String, Result<T>> lastGood = new ConcurrentHashMap<>();

    public ResilientLoader(String name, Duration ttl, long maxSize, int maxAttempts) {
        this.name = name;
        this.maxAttempts = Math.max(1, maxAttempts);
        this.fresh = Caffeine.newBuilder()
                .expireAfterWrite(ttl)
                .maximumSize(maxSize)
                .build();
    }

    public Result<T> get(String key, Supplier<T> loader) {
        try {
            return fresh.get(key, k -> {
                T value = loadWithRetry(k, loader);
                Result<T> result = new Result<>(value, Instant.now(), false);
                lastGood.put(k, result);
                return result;
            });
        } catch (RuntimeException e) {
            RuntimeException cause = unwrap(e);
            Result<T> good = lastGood.get(key);
            if (good != null) {
                log.warn("{}: serving stale data for '{}' after failure: {}", name, key, cause.getMessage());
                return new Result<>(good.value(), good.asOf(), true);
            }
            throw cause;
        }
    }

    /** Caffeine may wrap a loader exception; surface the original provider error. */
    private static RuntimeException unwrap(RuntimeException e) {
        if (e instanceof java.util.concurrent.CompletionException && e.getCause() instanceof RuntimeException re) {
            return re;
        }
        return e;
    }

    private T loadWithRetry(String key, Supplier<T> loader) {
        RuntimeException last = null;
        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                return loader.get();
            } catch (RuntimeException ex) {
                last = ex;
                if (attempt < maxAttempts) {
                    log.debug("{}: retry {}/{} for '{}': {}", name, attempt, maxAttempts, key, ex.getMessage());
                }
            }
        }
        throw last;
    }
}
