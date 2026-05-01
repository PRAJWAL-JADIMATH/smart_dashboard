package com.newsapp.controller;

import com.newsapp.dto.NewsResponseDTO;
import com.newsapp.service.NewsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller to expose news endpoints to our frontend.
 */
@RestController
@RequestMapping("/api/news")
@CrossOrigin(origins = "*") // Allows our React frontend to call this API without CORS errors
public class NewsController {

    private final NewsService newsService;

    public NewsController(NewsService newsService) {
        this.newsService = newsService;
    }

    /**
     * Endpoint: GET /api/news/top-headlines?category=technology
     */
    @GetMapping("/top-headlines")
    public ResponseEntity<NewsResponseDTO> getTopHeadlines(
            @RequestParam(defaultValue = "general") String category) {
        
        NewsResponseDTO news = newsService.fetchTopHeadlines(category);
        return ResponseEntity.ok(news);
    }
}
