package com.marketpulse.backend.dto;

import lombok.*;

@Getter @Builder
public class SecFilingDto {
    private String formType;
    private String filedDate;
    private String filingUrl;
}
