package com.marketpulse.backend.service;

import com.marketpulse.backend.dto.FundamentalsDto;
import com.marketpulse.backend.provider.FundamentalsProvider;
import org.springframework.stereotype.Service;

@Service
public class FundamentalsService {
    private final FundamentalsProvider fundamentalsProvider;

    public FundamentalsService(FundamentalsProvider fundamentalsProvider) {
        this.fundamentalsProvider = fundamentalsProvider;
    }

    public FundamentalsDto getFundamentals(String ticker) {
        return fundamentalsProvider.getFundamentals(ticker.toUpperCase());
    }
}
