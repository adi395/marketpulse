package com.marketpulse.backend.dto;

import lombok.*;

@Getter @Builder
public class NewsArticleDto {
    private String headline;
    private String source;
    private String summary;
    private String url;
    private String imageUrl;
    private Long publishedAt;
}
