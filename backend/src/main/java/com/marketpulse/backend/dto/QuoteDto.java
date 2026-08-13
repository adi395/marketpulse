package com.marketpulse.backend.dto;

import lombok.*;

@Getter @Builder
public class QuoteDto {
    private String ticker;
    private Double currentPrice;
    private Double change;
    private Double percentChange;
    private Double dayHigh;
    private Double dayLow;
    private Double openPrice;
    private Double previousClose;
    private Long timestamp;
}
