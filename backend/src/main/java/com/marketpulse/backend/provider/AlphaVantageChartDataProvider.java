package com.marketpulse.backend.provider;

import com.marketpulse.backend.dto.PriceBarDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Component
public class AlphaVantageChartDataProvider implements ChartDataProvider {

    private final RestClient restClient;

    @Value("${alphavantage.api.key}")
    private String apiKey;

    @Value("${alphavantage.base-url}")
    private String baseUrl;

    public AlphaVantageChartDataProvider(RestClient restClient) {
        this.restClient = restClient;
    }

    /**
     * Fetches the full daily history. Alpha Vantage charges the same single
     * request for {@code outputsize=full} as for compact, so we pull the whole
     * series and let the service slice it into time ranges. Caching, retry and
     * stale fallback are handled by the ChartService's ResilientLoader.
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<PriceBarDto> getHistoricalPrices(String ticker) {
        Map<String, Object> response = restClient.get()
                .uri(baseUrl + "?function=TIME_SERIES_DAILY&outputsize=full&symbol={ticker}&apikey={key}", ticker, apiKey)
                .retrieve()
                .body(Map.class);

        if (response == null) {
            throw new RuntimeException("No historical price data returned for " + ticker);
        }
        // Alpha Vantage returns 200 OK with a "Note"/"Information" message instead
        // of data when the free-tier rate limit is hit; surface that clearly.
        if (response.containsKey("Note") || response.containsKey("Information")) {
            throw new RuntimeException("Alpha Vantage rate limit reached for " + ticker);
        }
        if (!response.containsKey("Time Series (Daily)")) {
            throw new RuntimeException("No historical price data returned for " + ticker);
        }

        Map<String, Map<String, String>> series =
                (Map<String, Map<String, String>>) response.get("Time Series (Daily)");

        // TreeMap sorts dates ascending automatically (oldest first, best for charting)
        Map<String, Map<String, String>> sorted = new TreeMap<>(series);

        List<PriceBarDto> bars = new ArrayList<>();
        for (Map.Entry<String, Map<String, String>> entry : sorted.entrySet()) {
            Map<String, String> day = entry.getValue();
            bars.add(PriceBarDto.builder()
                    .date(entry.getKey())
                    .open(Double.parseDouble(day.get("1. open")))
                    .high(Double.parseDouble(day.get("2. high")))
                    .low(Double.parseDouble(day.get("3. low")))
                    .close(Double.parseDouble(day.get("4. close")))
                    .volume(Long.parseLong(day.get("5. volume")))
                    .build());
        }

        return bars;
    }
}
