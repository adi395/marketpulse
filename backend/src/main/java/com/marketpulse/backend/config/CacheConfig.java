package com.marketpulse.backend.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

/**
 * In-memory caching for responses that come from rate-limited external APIs
 * (Finnhub, Alpha Vantage). Each cache gets its own time-to-live tuned to how
 * often the underlying data actually changes, so we serve fresh-enough data
 * while staying well under the providers' free-tier request limits.
 */
@Configuration
@EnableCaching
public class CacheConfig {

    public static final String QUOTES = "quotes";
    public static final String CHARTS = "charts";
    public static final String FUNDAMENTALS = "fundamentals";
    public static final String NEWS = "news";
    public static final String EARNINGS = "earnings";
    public static final String FILINGS = "filings";

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager manager = new CaffeineCacheManager();
        manager.setAllowNullValues(false);

        // Intraday prices move constantly, so keep quotes only briefly.
        manager.registerCustomCache(QUOTES, build(Duration.ofSeconds(30), 500));
        // Daily OHLC bars change once a day; Alpha Vantage is the tightest
        // free tier (25 calls/day), so cache aggressively.
        manager.registerCustomCache(CHARTS, build(Duration.ofHours(6), 500));
        manager.registerCustomCache(FUNDAMENTALS, build(Duration.ofHours(1), 500));
        manager.registerCustomCache(NEWS, build(Duration.ofMinutes(15), 500));
        manager.registerCustomCache(EARNINGS, build(Duration.ofHours(6), 500));
        manager.registerCustomCache(FILINGS, build(Duration.ofHours(6), 500));

        return manager;
    }

    private com.github.benmanes.caffeine.cache.Cache<Object, Object> build(Duration ttl, int maxSize) {
        return Caffeine.newBuilder()
                .expireAfterWrite(ttl)
                .maximumSize(maxSize)
                .recordStats()
                .build();
    }
}
