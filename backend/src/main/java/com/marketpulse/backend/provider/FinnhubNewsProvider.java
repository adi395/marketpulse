package com.marketpulse.backend.provider;

import com.marketpulse.backend.dto.NewsArticleDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class FinnhubNewsProvider implements NewsProvider {

    private final RestClient restClient;

    @Value("${finnhub.api.key}")
    private String apiKey;

    @Value("${finnhub.base-url}")
    private String baseUrl;

    public FinnhubNewsProvider(RestClient restClient) {
        this.restClient = restClient;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<NewsArticleDto> getCompanyNews(String ticker) {
        DateTimeFormatter fmt = DateTimeFormatter.ISO_LOCAL_DATE;
        String to = LocalDate.now().format(fmt);
        String from = LocalDate.now().minusDays(7).format(fmt);

        List<Map<String, Object>> response = restClient.get()
                .uri(baseUrl + "/company-news?symbol={ticker}&from={from}&to={to}&token={key}",
                        ticker, from, to, apiKey)
                .retrieve()
                .body(List.class);

        if (response == null) {
            throw new RuntimeException("No news data returned for " + ticker);
        }

        return response.stream()
                .limit(10)
                .map(article -> NewsArticleDto.builder()
                        .headline((String) article.get("headline"))
                        .source((String) article.get("source"))
                        .summary((String) article.get("summary"))
                        .url((String) article.get("url"))
                        .imageUrl((String) article.get("image"))
                        .publishedAt(article.get("datetime") != null
                                ? ((Number) article.get("datetime")).longValue() : null)
                        .build())
                .collect(Collectors.toList());
    }
}
