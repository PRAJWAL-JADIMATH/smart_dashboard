package com.newsapp.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * JPA Entity representing the 'bookmarks' table in the database.
 */
@Entity
@Table(name = "bookmarks")
@Data
@NoArgsConstructor
public class Bookmark {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    
    @Column(columnDefinition = "TEXT") // Use TEXT because descriptions can be very long
    private String description;
    
    @Column(length = 500) // URLs can be quite long
    private String url;
    
    @Column(length = 500)
    private String urlToImage;

    // Many Bookmarks can belong to a single User (Many-to-1 Relationship)
    // FetchType.LAZY improves performance by only loading the User data when explicitly requested
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false) // This creates a Foreign Key column
    @com.fasterxml.jackson.annotation.JsonIgnore
    private User user;

    @Transient
    private String generatedSummary;
}
