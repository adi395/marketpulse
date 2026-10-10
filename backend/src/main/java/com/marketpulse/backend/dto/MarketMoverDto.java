package com.marketpulse.backend.dto;

import lombok.*;

@Getter @Builder
public class MarketMoverDto {
    private String ticker;
    private String name;
    private String sector;
    private String exchange;
    private Double currentPrice;
    private Double change;
    private Double percentChange;
}
