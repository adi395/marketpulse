package com.marketpulse.backend.util;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TickersTest {

    @Test
    void normalizesValidTickersToUpperCase() {
        assertThat(Tickers.require("aapl")).isEqualTo("AAPL");
        assertThat(Tickers.require("BRK.B")).isEqualTo("BRK.B");
        assertThat(Tickers.require("rds-a")).isEqualTo("RDS-A");
    }

    @Test
    void rejectsMalformedInput() {
        assertThatThrownBy(() -> Tickers.require(null)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> Tickers.require("")).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> Tickers.require("'; DROP TABLE companies;--"))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> Tickers.require("TOOOOOLONGGG")).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> Tickers.require("1AAPL")).isInstanceOf(IllegalArgumentException.class);
    }
}
