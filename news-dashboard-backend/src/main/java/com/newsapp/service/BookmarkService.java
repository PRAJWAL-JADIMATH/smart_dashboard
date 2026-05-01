package com.newsapp.service;

import com.newsapp.dto.BookmarkRequestDTO;
import com.newsapp.dto.SummaryRequestDTO;
import com.newsapp.entity.Bookmark;
import com.newsapp.entity.User;
import com.newsapp.repository.BookmarkRepository;
import com.newsapp.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service class handling the business logic for Bookmarks.
 */
@Service
public class BookmarkService {

    private final BookmarkRepository bookmarkRepository;
    private final UserRepository userRepository;
    private final AIService aiService;
    private final EmailService emailService;

    public BookmarkService(BookmarkRepository bookmarkRepository, UserRepository userRepository, AIService aiService, EmailService emailService) {
        this.bookmarkRepository = bookmarkRepository;
        this.userRepository = userRepository;
        this.aiService = aiService;
        this.emailService = emailService;
    }

    // CREATE
    public Bookmark saveBookmark(BookmarkRequestDTO request) {
        // 1. Verify the user exists
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 2. Map DTO to Entity
        Bookmark bookmark = new Bookmark();
        bookmark.setTitle(request.getTitle());
        bookmark.setDescription(request.getDescription());
        bookmark.setUrl(request.getUrl());
        bookmark.setUrlToImage(request.getUrlToImage());
        
        // 3. Set the relationship
        bookmark.setUser(user);

        // 4. Save to DB
        Bookmark savedBookmark = bookmarkRepository.save(bookmark);

        // 5. Always Generate Summary (errors handled inside AIService)
        SummaryRequestDTO summaryReq = new SummaryRequestDTO();
        summaryReq.setArticleTitle(request.getTitle());
        summaryReq.setArticleDescription(request.getDescription());
        
        String aiSummary = aiService.generateSummary(summaryReq);
        savedBookmark.setGeneratedSummary(aiSummary);

        return savedBookmark;
    }

    // READ
    public List<Bookmark> getUserBookmarks(Long userId) {
        return bookmarkRepository.findByUserId(userId);
    }

    // DELETE
    public void deleteBookmark(Long bookmarkId) {
        // Spring Data JPA makes deletion a one-liner!
        bookmarkRepository.deleteById(bookmarkId);
    }
}
