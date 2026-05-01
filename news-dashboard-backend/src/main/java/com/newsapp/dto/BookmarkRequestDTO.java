package com.newsapp.dto;

import lombok.Data;

@Data
public class BookmarkRequestDTO {
    // MENTOR NOTE: In a fully secured production app, we wouldn't send the userId from the frontend.
    // Instead, we would extract the userId securely from the JWT token in the backend. 
    // We are including it here to keep the architecture beginner-friendly.
    private Long userId; 
    
    private String title;
    private String description;
    private String url;
    private String urlToImage;
    private boolean sendEmail;
}
