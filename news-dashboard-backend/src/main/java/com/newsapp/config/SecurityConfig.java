package com.newsapp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configuration for Spring Security.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // Bean to hash passwords securely using BCrypt algorithm
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Configures HTTP security (endpoints access)
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // For beginner-friendly development, we disable CSRF and permit all requests.
        // In a full production app, we would restrict specific endpoints with a JWT Filter here.
        http.csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(auth -> auth
                .anyRequest().permitAll()
            );
        return http.build();
    }
}
