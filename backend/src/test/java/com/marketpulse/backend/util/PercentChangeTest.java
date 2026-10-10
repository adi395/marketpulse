package com.marketpulse.backend.util;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PercentChangeTest {

    @Test
    void computesPositiveChange() {
        assertThat(PercentChange.compute(110.0, 100.0)).isEqualTo(10.0);
    }

    @Test
    void computesNegativeChange() {
        assertThat(PercentChange.compute(90.0, 100.0)).isEqualTo(-10.0);
    }

    @Test
    void zeroChangeIsZeroNotNull() {
        assertThat(PercentChange.compute(100.0, 100.0)).isEqualTo(0.0);
    }

    @Test
    void returnsNullWhenCurrentIsMissing() {
        assertThat(PercentChange.compute(null, 100.0)).isNull();
    }

    @Test
    void returnsNullWhenPreviousCloseIsMissing() {
        assertThat(PercentChange.compute(100.0, null)).isNull();
    }

    @Test
    void returnsNullOnZeroDenominatorInsteadOfInfinity() {
        assertThat(PercentChange.compute(100.0, 0.0)).isNull();
    }
}
