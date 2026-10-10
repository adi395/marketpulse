package com.marketpulse.backend.util;

import java.util.regex.Pattern;

/** Validation for user-supplied ticker symbols before they reach providers. */
public final class Tickers {

    // 1-10 chars: a leading letter, then letters/digits/dot/dash (e.g. BRK.B, RDS-A).
    private static final Pattern VALID = Pattern.compile("^[A-Za-z][A-Za-z0-9.\\-]{0,9}$");

    private Tickers() {}

    /**
     * Returns the normalized (upper-cased) ticker, or throws
     * {@link IllegalArgumentException} (mapped to HTTP 400) if it is malformed.
     */
    public static String require(String ticker) {
        if (ticker == null || !VALID.matcher(ticker).matches()) {
            throw new IllegalArgumentException("Invalid ticker symbol: " + ticker);
        }
        return ticker.toUpperCase();
    }
}
