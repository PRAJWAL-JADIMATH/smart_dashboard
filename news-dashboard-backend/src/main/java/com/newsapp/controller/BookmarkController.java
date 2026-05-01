package com.newsapp.controller;

import com.newsapp.dto.BookmarkRequestDTO;
import com.newsapp.dto.EmailRequestDTO;
import com.newsapp.entity.Bookmark;
import com.newsapp.service.BookmarkService;
import com.newsapp.service.EmailService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for managing bookmarks.
 */
@RestController
@RequestMapping("/api/bookmarks")
@CrossOrigin(origins = "*")
public class BookmarkController {

    private final BookmarkService bookmarkService;
    private final EmailService emailService;

    public BookmarkController(BookmarkService bookmarkService, EmailService emailService) {
        this.bookmarkService = bookmarkService;
        this.emailService = emailService;
    }

    // POST /api/bookmarks
    @PostMapping
    public ResponseEntity<Bookmark> saveBookmark(@RequestBody BookmarkRequestDTO request) {
        Bookmark savedBookmark = bookmarkService.saveBookmark(request);
        return ResponseEntity.ok(savedBookmark);
    }

    // POST /api/bookmarks/email
    @PostMapping("/email")
    public ResponseEntity<String> sendBookmarkEmail(@RequestBody EmailRequestDTO request) {
        emailService.sendSummaryEmail(request.getToEmail(), request.getTitle(), request.getSummary(), request.getUrl());
        return ResponseEntity.ok("Email sent successfully.");
    }

    // GET /api/bookmarks/user/{userId}
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Bookmark>> getUserBookmarks(@PathVariable Long userId) {
        List<Bookmark> bookmarks = bookmarkService.getUserBookmarks(userId);
        return ResponseEntity.ok(bookmarks);
    }

    // DELETE /api/bookmarks/{bookmarkId}
    @DeleteMapping("/{bookmarkId}")
    public ResponseEntity<String> deleteBookmark(@PathVariable Long bookmarkId) {
        bookmarkService.deleteBookmark(bookmarkId);
        return ResponseEntity.ok("Bookmark deleted successfully.");
    }
}
