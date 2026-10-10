package com.marketpulse.backend.service;

import com.marketpulse.backend.dto.QuoteDto;
import com.marketpulse.backend.provider.MarketDataProvider;
import com.marketpulse.backend.resilience.ResilientLoader;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class QuoteService {
    private final MarketDataProvider marketDataProvider;

    // 30s freshness; one retry for a transient Finnhub blip; last-good fallback.
    private final ResilientLoader<QuoteDto> loader =
            new ResilientLoader<>("quotes", Duration.ofSeconds(30), 1000, 2);

    public QuoteService(MarketDataProvider marketDataProvider) {
        this.marketDataProvider = marketDataProvider;
    }

    public ResilientLoader.Result<QuoteDto> getQuote(String ticker) {
        String key = ticker.toUpperCase();
        return loader.get(key, () -> marketDataProvider.getQuote(key));
    }
}
