package com.marketpulse.backend.dto;

import lombok.*;

@Getter @Builder
public class FundamentalsDto {
    // Valuation
    private Double peTTM;
    private Double peNormalized;
    private Double priceToSalesTTM;
    private Double priceToBook;

    // Profitability
    private Double grossMarginTTM;
    private Double operatingMarginTTM;
    private Double netMarginTTM;
    private Double epsTTM;

    // Growth
    private Double epsGrowthYoY;
    private Double revenueGrowthYoY;

    // Balance sheet
    private Double debtToEquity;

    // 52-week range
    private Double week52High;
    private Double week52Low;
}
