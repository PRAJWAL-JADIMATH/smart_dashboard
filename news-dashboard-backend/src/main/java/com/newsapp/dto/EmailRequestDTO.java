package com.newsapp.dto;

import lombok.Data;

@Data
public class EmailRequestDTO {
    private String toEmail;
    private String title;
    private String summary;
    private String url;
}
