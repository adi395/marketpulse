package com.marketpulse.backend.provider;

import com.marketpulse.backend.dto.QuoteDto;
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

        return QuoteDto.builder()
                .ticker(ticker)
                .currentPrice(toDouble(response.get("c")))
                .change(toDouble(response.get("d")))
                .percentChange(toDouble(response.get("dp")))
                .dayHigh(toDouble(response.get("h")))
                .dayLow(toDouble(response.get("l")))
                .openPrice(toDouble(response.get("o")))
                .previousClose(toDouble(response.get("pc")))
                .timestamp(response.get("t") != null ? ((Number) response.get("t")).longValue() : null)
                .build();
    }

    private Double toDouble(Object val) {
        return val != null ? ((Number) val).doubleValue() : null;
    }
}
