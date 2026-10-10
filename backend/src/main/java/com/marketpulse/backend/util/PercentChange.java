package com.marketpulse.backend.util;

/** Percent-change computation with explicit handling of missing/degenerate inputs. */
public final class PercentChange {

    private PercentChange() {}

    /**
     * Percent change from {@code previousClose} to {@code current}, or
     * {@code null} when it cannot be computed — either input missing, or a
     * previous close of zero (which would divide by zero). Returning null rather
     * than 0, NaN or Infinity keeps a bad input from being shown as a real move.
     */
    public static Double compute(Double current, Double previousClose) {
        if (current == null || previousClose == null || previousClose == 0.0) {
            return null;
        }
        return (current - previousClose) / previousClose * 100.0;
    }
}
