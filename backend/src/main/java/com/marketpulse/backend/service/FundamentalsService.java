package com.marketpulse.backend.service;

import com.marketpulse.backend.config.CacheConfig;
import com.marketpulse.backend.dto.FundamentalsDto;
import com.marketpulse.backend.provider.FundamentalsProvider;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class FundamentalsService {
    private final FundamentalsProvider fundamentalsProvider;

    public FundamentalsService(FundamentalsProvider fundamentalsProvider) {
        this.fundamentalsProvider = fundamentalsProvider;
    }

    @Cacheable(cacheNames = CacheConfig.FUNDAMENTALS, key = "#ticker.toUpperCase()")
    public FundamentalsDto getFundamentals(String ticker) {
        return fundamentalsProvider.getFundamentals(ticker.toUpperCase());
    }
}
