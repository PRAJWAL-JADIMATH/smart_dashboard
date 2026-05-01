package com.newsapp.repository;

import com.newsapp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Interface for database operations on the User entity.
 * Spring Data JPA automatically provides implementations for standard CRUD operations (save, findById, delete, etc.).
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    // Magic of Spring Data JPA: It automatically generates the SQL query 
    // simply based on the method name "findByEmail"!
    // SQL: SELECT * FROM users WHERE email = ?
    Optional<User> findByEmail(String email);
}
