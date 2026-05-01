package com.newsapp.dto;

import lombok.Data;

@Data
public class AuthResponseDTO {
    private String token;
    private Long id;
    private String name;
    private String email;
}
