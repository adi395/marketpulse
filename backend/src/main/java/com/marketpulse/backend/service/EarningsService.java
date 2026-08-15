package com.marketpulse.backend.service;

import com.marketpulse.backend.dto.EarningsDto;
import com.marketpulse.backend.provider.EarningsProvider;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EarningsService {
    private final EarningsProvider earningsProvider;

    public EarningsService(EarningsProvider earningsProvider) {
        this.earningsProvider = earningsProvider;
    }

    public List<EarningsDto> getEarnings(String ticker) {
        return earningsProvider.getEarnings(ticker.toUpperCase());
    }
}
