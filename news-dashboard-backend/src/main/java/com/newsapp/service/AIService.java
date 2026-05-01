package com.newsapp.service;

import com.newsapp.dto.SummaryRequestDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

/**
 * Service to interact with the OpenAI API for article summarization.
 */
@Service
public class AIService {

    private final RestTemplate restTemplate;

    @Value("${app.openai.key}")
    private String openAiKey;

    public AIService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String generateSummary(SummaryRequestDTO request) {
        if (openAiKey == null || openAiKey.isBlank() || openAiKey.contains("your_key_here")) {
            return "MOCK AI SUMMARY: This article discusses '" + request.getArticleTitle() + "'.";
        }

        System.out.println("=== NVIDIA NIM API Call ===");
        System.out.println("Using key prefix: " + openAiKey.substring(0, Math.min(10, openAiKey.length())) + "...");

        String url = "https://integrate.api.nvidia.com/v1/chat/completions";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(openAiKey);

        String prompt = "Act as an expert intelligence analyst. Summarize this news article and explain 'Why it matters' in 2 sentences. "
                + "Title: " + request.getArticleTitle()
                + ". Description: " + request.getArticleDescription();

        Map<String, Object> requestBody = Map.of(
                "model", "meta/llama-3.1-8b-instruct",
                "messages", List.of(
                        Map.of("role", "user", "content", prompt)
                ),
                "max_tokens", 300,
                "temperature", 0.7
        );

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);
            Map<String, Object> responseBody = response.getBody();
            System.out.println("NVIDIA NIM response status: " + response.getStatusCode());

            List<Map<String, Object>> choices = (List<Map<String, Object>>) responseBody.get("choices");
            Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
            return (String) message.get("content");

        } catch (HttpClientErrorException | HttpServerErrorException e) {
            System.err.println("=== NVIDIA NIM HTTP Error ===");
            System.err.println("Status: " + e.getStatusCode());
            System.err.println("Response body: " + e.getResponseBodyAsString());
            return "AI Summary unavailable. Backend error: " + e.getStatusCode() + " — check server logs for details.";
        } catch (Exception e) {
            System.err.println("=== NVIDIA NIM Unexpected Error ===");
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            e.printStackTrace();
            return "AI Summary unavailable: " + e.getMessage();
        }
    }
}
