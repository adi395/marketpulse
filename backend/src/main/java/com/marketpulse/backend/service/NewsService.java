package com.marketpulse.backend.service;

import com.marketpulse.backend.dto.NewsArticleDto;
import com.marketpulse.backend.provider.NewsProvider;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NewsService {
    private final NewsProvider newsProvider;

    public NewsService(NewsProvider newsProvider) {
        this.newsProvider = newsProvider;
    }

    public List<NewsArticleDto> getCompanyNews(String ticker) {
        return newsProvider.getCompanyNews(ticker.toUpperCase());
    }
}
