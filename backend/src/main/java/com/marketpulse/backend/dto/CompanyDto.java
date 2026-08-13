package com.marketpulse.backend.dto;

import lombok.*;

@Getter @Builder
public class CompanyDto {
    private String ticker;
    private String name;
    private String sector;
    private String exchange;
}
