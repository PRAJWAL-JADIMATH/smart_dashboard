package com.newsapp.dto;

import lombok.Data;

/**
 * Data Transfer Object representing a single article.
 * The fields exactly match the JSON response from NewsAPI.
 */
@Data
public class ArticleDTO {
    private String title;
    private String description;
    private String url;
    private String urlToImage;
    private String publishedAt;
}
