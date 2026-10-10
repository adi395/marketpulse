package com.marketpulse.backend.provider;

import com.marketpulse.backend.dto.QuoteDto;
import com.marketpulse.backend.util.PercentChange;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Component
public class FinnhubMarketDataProvider implements MarketDataProvider {

    private final RestClient restClient;

    @Value("${finnhub.api.key}")
    private String apiKey;

    @Value("${finnhub.base-url}")
    private String baseUrl;

    public FinnhubMarketDataProvider(RestClient restClient) {
        this.restClient = restClient;
    }

    @Override
    public QuoteDto getQuote(String ticker) {
        Map<String, Object> response = restClient.get()
                .uri(baseUrl + "/quote?symbol={ticker}&token={key}", ticker, apiKey)
                .retrieve()
                .body(Map.class);

        if (response == null || response.get("c") == null) {
            throw new RuntimeException("No quote data returned for " + ticker);
        }

        Double current = toDouble(response.get("c"));
        Double previousClose = toDouble(response.get("pc"));
        Double percentChange = toDouble(response.get("dp"));
        // Fall back to computing percent change ourselves if the provider omits it.
        if (percentChange == null) {
            percentChange = PercentChange.compute(current, previousClose);
        }

        return QuoteDto.builder()
                .ticker(ticker)
                .currentPrice(current)
                .change(toDouble(response.get("d")))
                .percentChange(percentChange)
                .dayHigh(toDouble(response.get("h")))
                .dayLow(toDouble(response.get("l")))
                .openPrice(toDouble(response.get("o")))
                .previousClose(previousClose)
                .timestamp(response.get("t") != null ? ((Number) response.get("t")).longValue() : null)
                .build();
    }

    private Double toDouble(Object val) {
        return val != null ? ((Number) val).doubleValue() : null;
    }
}
