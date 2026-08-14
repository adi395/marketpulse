package com.marketpulse.backend.provider;

import com.marketpulse.backend.dto.SecFilingDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Comparator;
import java.util.stream.Collectors;

@Component
public class FinnhubFilingsProvider implements FilingsProvider {

    private static final Set<String> RELEVANT_FORMS = Set.of("10-K", "10-Q", "8-K", "DEF 14A");

    private final RestClient restClient;

    @Value("${finnhub.api.key}")
    private String apiKey;

    @Value("${finnhub.base-url}")
    private String baseUrl;

    public FinnhubFilingsProvider(RestClient restClient) {
        this.restClient = restClient;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<SecFilingDto> getFilings(String ticker) {
        List<Map<String, Object>> response = restClient.get()
                .uri(baseUrl + "/stock/filings?symbol={ticker}&token={key}", ticker, apiKey)
                .retrieve()
                .body(List.class);

        if (response == null) {
            throw new RuntimeException("No filings data returned for " + ticker);
        }

        return response.stream()
                .filter(f -> RELEVANT_FORMS.contains((String) f.get("form")))
                .sorted(Comparator.comparing((Map<String, Object> f) -> (String) f.get("filedDate")).reversed())
                .limit(10)
                .map(f -> SecFilingDto.builder()
                        .formType((String) f.get("form"))
                        .filedDate((String) f.get("filedDate"))
                        .filingUrl((String) f.get("filingUrl"))
                        .build())
                .collect(Collectors.toList());
    }
}
