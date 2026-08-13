package com.marketpulse.backend.provider;

import com.marketpulse.backend.dto.QuoteDto;

public interface MarketDataProvider {
    QuoteDto getQuote(String ticker);
}
