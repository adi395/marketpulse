package com.marketpulse.backend.provider;

import com.marketpulse.backend.dto.NewsArticleDto;
import java.util.List;

public interface NewsProvider {
    List<NewsArticleDto> getCompanyNews(String ticker);
}
