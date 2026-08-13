package com.marketpulse.backend.dto;

import lombok.*;

@Getter @Builder
public class PriceBarDto {
    private String date;
    private Double open;
    private Double high;
    private Double low;
    private Double close;
    private Long volume;
}
