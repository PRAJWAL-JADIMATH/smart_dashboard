package com.newsapp.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.ArrayList;
import java.util.List;

/**
 * JPA Entity representing the 'users' table in the database.
 */
@Entity
@Table(name = "users") // We use 'users' because 'user' is often a reserved keyword in SQL
@Data
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-increments the ID (Primary Key)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String name;

    // A User can have multiple Bookmarks (1-to-Many Relationship)
    // 'mappedBy' links this to the 'user' field in the Bookmark entity
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @com.fasterxml.jackson.annotation.JsonIgnore
    private List<Bookmark> bookmarks = new ArrayList<>();
}
