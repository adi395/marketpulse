package com.marketpulse.backend.service;

import com.marketpulse.backend.config.CacheConfig;
import com.marketpulse.backend.dto.QuoteDto;
import com.marketpulse.backend.provider.MarketDataProvider;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class QuoteService {
    private final MarketDataProvider marketDataProvider;

    public QuoteService(MarketDataProvider marketDataProvider) {
        this.marketDataProvider = marketDataProvider;
    }

    @Cacheable(cacheNames = CacheConfig.QUOTES, key = "#ticker.toUpperCase()")
    public QuoteDto getQuote(String ticker) {
        return marketDataProvider.getQuote(ticker.toUpperCase());
    }
}
