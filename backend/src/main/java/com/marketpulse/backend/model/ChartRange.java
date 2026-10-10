package com.marketpulse.backend.model;

/**
 * Supported price-chart time windows. Days are approximate calendar days used
 * to compute a cutoff date; MAX returns the full available history.
 */
public enum ChartRange {
    M1(30),
    M3(90),
    M6(180),
    Y1(365),
    MAX(Integer.MAX_VALUE);

    private final int days;

    ChartRange(int days) {
        this.days = days;
    }

    public int days() {
        return days;
    }

    /** Parses a user-supplied range string, throwing on anything unsupported. */
    public static ChartRange fromParam(String raw) {
        if (raw == null || raw.isBlank()) {
            return M6;
        }
        return switch (raw.trim().toUpperCase()) {
            case "1M" -> M1;
            case "3M" -> M3;
            case "6M" -> M6;
            case "1Y" -> Y1;
            case "MAX", "ALL" -> MAX;
            default -> throw new IllegalArgumentException(
                    "Unsupported range '" + raw + "'. Supported: 1M, 3M, 6M, 1Y, MAX");
        };
    }
}
