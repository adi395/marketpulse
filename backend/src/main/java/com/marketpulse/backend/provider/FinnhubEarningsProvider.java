package com.marketpulse.backend.provider;

import com.marketpulse.backend.dto.EarningsDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class FinnhubEarningsProvider implements EarningsProvider {

    private final RestClient restClient;

    @Value("${finnhub.api.key}")
    private String apiKey;

    @Value("${finnhub.base-url}")
    private String baseUrl;

    public FinnhubEarningsProvider(RestClient restClient) {
        this.restClient = restClient;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<EarningsDto> getEarnings(String ticker) {
        List<Map<String, Object>> response = restClient.get()
                .uri(baseUrl + "/stock/earnings?symbol={ticker}&token={key}", ticker, apiKey)
                .retrieve()
                .body(List.class);

        if (response == null) {
            throw new RuntimeException("No earnings data returned for " + ticker);
        }

        return response.stream()
                .limit(4)
                .map(e -> EarningsDto.builder()
                        .period((String) e.get("period"))
                        .year(e.get("year") != null ? ((Number) e.get("year")).intValue() : null)
                        .quarter(e.get("quarter") != null ? ((Number) e.get("quarter")).intValue() : null)
                        .epsActual(e.get("actual") != null ? ((Number) e.get("actual")).doubleValue() : null)
                        .epsEstimate(e.get("estimate") != null ? ((Number) e.get("estimate")).doubleValue() : null)
                        .surprisePercent(e.get("surprisePercent") != null ? ((Number) e.get("surprisePercent")).doubleValue() : null)
                        .build())
                .collect(Collectors.toList());
    }
}
