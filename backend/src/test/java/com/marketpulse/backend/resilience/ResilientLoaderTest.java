package com.marketpulse.backend.resilience;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ResilientLoaderTest {

    // ttl=0 forces every get() to reload, which makes fresh/stale behavior deterministic to test.
    private <T> ResilientLoader<T> loader(int maxAttempts) {
        return new ResilientLoader<>("test", Duration.ZERO, 100, maxAttempts);
    }

    @Test
    void returnsFreshValueNotStale() {
        ResilientLoader<String> l = loader(1);
        ResilientLoader.Result<String> r = l.get("k", () -> "v1");
        assertThat(r.value()).isEqualTo("v1");
        assertThat(r.stale()).isFalse();
        assertThat(r.asOf()).isNotNull();
    }

    @Test
    void servesLastGoodAsStaleAfterFailure() {
        ResilientLoader<String> l = loader(1);
        assertThat(l.get("k", () -> "good").stale()).isFalse();

        ResilientLoader.Result<String> r = l.get("k", () -> {
            throw new RuntimeException("provider down");
        });
        assertThat(r.value()).isEqualTo("good");
        assertThat(r.stale()).isTrue();
    }

    @Test
    void propagatesWhenNoPriorSuccessExists() {
        ResilientLoader<String> l = loader(1);
        assertThatThrownBy(() -> l.get("k", () -> {
            throw new RuntimeException("provider down");
        })).isInstanceOf(RuntimeException.class).hasMessageContaining("provider down");
    }

    @Test
    void retriesUpToMaxAttemptsThenSucceeds() {
        ResilientLoader<String> l = loader(3);
        AtomicInteger calls = new AtomicInteger();
        ResilientLoader.Result<String> r = l.get("k", () -> {
            if (calls.incrementAndGet() < 3) throw new RuntimeException("transient");
            return "ok";
        });
        assertThat(r.value()).isEqualTo("ok");
        assertThat(calls.get()).isEqualTo(3);
    }

    @Test
    void doesNotRetryWhenMaxAttemptsIsOne() {
        ResilientLoader<String> l = loader(1);
        AtomicInteger calls = new AtomicInteger();
        assertThatThrownBy(() -> l.get("k", () -> {
            calls.incrementAndGet();
            throw new RuntimeException("down");
        })).isInstanceOf(RuntimeException.class);
        assertThat(calls.get()).isEqualTo(1);
    }
}
