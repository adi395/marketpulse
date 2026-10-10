package com.marketpulse.backend.service;

import com.marketpulse.backend.config.CacheConfig;
import com.marketpulse.backend.dto.EarningsDto;
import com.marketpulse.backend.provider.EarningsProvider;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EarningsService {
    private final EarningsProvider earningsProvider;

    public EarningsService(EarningsProvider earningsProvider) {
        this.earningsProvider = earningsProvider;
    }

    @Cacheable(cacheNames = CacheConfig.EARNINGS, key = "#ticker.toUpperCase()")
    public List<EarningsDto> getEarnings(String ticker) {
        return earningsProvider.getEarnings(ticker.toUpperCase());
    }
}
