package com.newsapp.dto;

import lombok.Data;
import java.util.List;

/**
 * Data Transfer Object representing the full response from NewsAPI.
 */
@Data
public class NewsResponseDTO {
    private String status;
    private int totalResults;
    private List<ArticleDTO> articles;
}
