package com.marketpulse.backend.provider;

import com.marketpulse.backend.dto.FundamentalsDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Component
public class FinnhubFundamentalsProvider implements FundamentalsProvider {

    private final RestClient restClient;

    @Value("${finnhub.api.key}")
    private String apiKey;

    @Value("${finnhub.base-url}")
    private String baseUrl;

    public FinnhubFundamentalsProvider(RestClient restClient) {
        this.restClient = restClient;
    }

    @Override
    @SuppressWarnings("unchecked")
    public FundamentalsDto getFundamentals(String ticker) {
        Map<String, Object> response = restClient.get()
                .uri(baseUrl + "/stock/metric?symbol={ticker}&metric=all&token={key}", ticker, apiKey)
                .retrieve()
                .body(Map.class);

        if (response == null || response.get("metric") == null) {
            throw new RuntimeException("No fundamentals data returned for " + ticker);
        }

        Map<String, Object> metric = (Map<String, Object>) response.get("metric");

        return FundamentalsDto.builder()
                .peTTM(toDouble(metric.get("peBasicExclExtraTTM")))
                .peNormalized(toDouble(metric.get("peNormalizedAnnual")))
                .priceToSalesTTM(toDouble(metric.get("psTTM")))
                .priceToBook(toDouble(metric.get("pbQuarterly")))
                .grossMarginTTM(toDouble(metric.get("grossMarginTTM")))
                .operatingMarginTTM(toDouble(metric.get("operatingMarginTTM")))
                .netMarginTTM(toDouble(metric.get("netProfitMarginTTM")))
                .epsTTM(toDouble(metric.get("epsTTM")))
                .epsGrowthYoY(toDouble(metric.get("epsGrowthTTMYoy")))
                .revenueGrowthYoY(toDouble(metric.get("revenueGrowthTTMYoy")))
                .debtToEquity(toDouble(metric.get("totalDebt/totalEquityQuarterly")))
                .week52High(toDouble(metric.get("52WeekHigh")))
                .week52Low(toDouble(metric.get("52WeekLow")))
                .build();
    }

    private Double toDouble(Object val) {
        return val != null ? ((Number) val).doubleValue() : null;
    }
}
