package com.newsapp.controller;

import com.newsapp.dto.SummaryRequestDTO;
import com.newsapp.service.AIService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controller to expose AI features.
 */
@RestController
@RequestMapping("/api/ai")
@CrossOrigin(origins = "*")
public class AIController {

    private final AIService aiService;

    public AIController(AIService aiService) {
        this.aiService = aiService;
    }

    // Endpoint: POST /api/ai/summary
    @PostMapping("/summary")
    public ResponseEntity<Map<String, String>> getArticleSummary(@RequestBody SummaryRequestDTO request) {
        String summary = aiService.generateSummary(request);
        // Returning as JSON object { "summary": "..." }
        return ResponseEntity.ok(Map.of("summary", summary));
    }
}
