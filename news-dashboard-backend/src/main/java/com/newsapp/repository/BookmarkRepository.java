package com.newsapp.repository;

import com.newsapp.entity.Bookmark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Interface for database operations on the Bookmark entity.
 */
@Repository
public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {
    
    // Auto-generates: SELECT * FROM bookmarks WHERE user_id = ?
    List<Bookmark> findByUserId(Long userId);
}
