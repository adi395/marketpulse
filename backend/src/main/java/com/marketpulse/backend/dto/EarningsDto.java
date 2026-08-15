package com.marketpulse.backend.dto;

import lombok.*;

@Getter @Builder
public class EarningsDto {
    private String period;
    private Integer year;
    private Integer quarter;
    private Double epsActual;
    private Double epsEstimate;
    private Double surprisePercent;
}
