package com.newsapp.service;

import com.newsapp.dto.AuthRequestDTO;
import com.newsapp.dto.AuthResponseDTO;
import com.newsapp.dto.RegisterRequestDTO;
import com.newsapp.entity.User;
import com.newsapp.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // Dependency Injection
    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public AuthResponseDTO register(RegisterRequestDTO request) {
        // 1. Check if email exists
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email is already registered!");
        }

        // 2. Create User entity
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        
        // MENTOR NOTE: NEVER store raw passwords! We use BCrypt to hash it.
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        
        // 3. Save to database
        userRepository.save(user);
        
        // 4. Generate token and return
        return createAuthResponse(user);
    }

    public AuthResponseDTO login(AuthRequestDTO request) {
        // 1. Find user by email
        Optional<User> userOptional = userRepository.findByEmail(request.getEmail());
        
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            
            // 2. Compare the raw password with the hashed password in the DB
            if (passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                return createAuthResponse(user);
            }
        }
        throw new RuntimeException("Invalid email or password!");
    }

    private AuthResponseDTO createAuthResponse(User user) {
        // MENTOR NOTE: For simplicity in this project, we generate a UUID token.
        // In a highly strict production app, this would be a JWT (JSON Web Token) with an expiration time.
        String dummyToken = UUID.randomUUID().toString();
        
        AuthResponseDTO response = new AuthResponseDTO();
        response.setToken(dummyToken);
        response.setId(user.getId());
        response.setName(user.getName());
        response.setEmail(user.getEmail());
        return response;
    }
}
