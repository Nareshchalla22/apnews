package com.news.apnews.controller;

import com.news.apnews.service.S3service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/media")
public class MediaUploadController {

    @Autowired
    private S3service s3Service;

    // ── Upload image file (multipart) → returns S3 URL ───────────────────────
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadFile(
            @RequestParam("file")   MultipartFile file,
            @RequestParam(value = "folder", defaultValue = "news") String folder) {
        try {
            if (file.isEmpty())
                return ResponseEntity.badRequest().body(Map.of("message", "File is empty"));

            long maxSize = 10 * 1024 * 1024; // 10 MB
            if (file.getSize() > maxSize)
                return ResponseEntity.badRequest().body(Map.of("message", "File too large (max 10MB)"));

            String url = s3Service.uploadFile(file, folder);
            return ResponseEntity.ok(Map.of(
                "url",     url,
                "message", "Upload successful",
                "folder",  folder
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(Map.of("message", "Upload failed: " + e.getMessage()));
        }
    }

    // ── Upload Base64 image → returns S3 URL ─────────────────────────────────
    @PostMapping("/upload-base64")
    public ResponseEntity<?> uploadBase64(@RequestBody Map<String, String> body) {
        try {
            String base64 = body.get("image");
            String folder = body.getOrDefault("folder", "news");

            if (base64 == null || base64.isBlank())
                return ResponseEntity.badRequest().body(Map.of("message", "No image data"));

            // Rough size check: base64 is ~33% larger than binary
            if (base64.length() > 14_000_000)
                return ResponseEntity.badRequest().body(Map.of("message", "Image too large (max 10MB)"));

            String url = s3Service.uploadBase64(base64, folder);
            if (url == null)
                return ResponseEntity.badRequest().body(Map.of("message", "Invalid base64 format"));

            return ResponseEntity.ok(Map.of("url", url, "message", "Upload successful"));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(Map.of("message", "Upload failed: " + e.getMessage()));
        }
    }

    // ── Delete image by URL ───────────────────────────────────────────────────
    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteFile(@RequestBody Map<String, String> body) {
        String url = body.get("url");
        if (url == null || url.isBlank())
            return ResponseEntity.badRequest().body(Map.of("message", "URL required"));

        s3Service.deleteByUrl(url);
        return ResponseEntity.ok(Map.of("message", "Deleted successfully"));
    }

    // ── Health check ──────────────────────────────────────────────────────────
    @GetMapping("/health")
    public ResponseEntity<?> health() {
        boolean s3Ok = s3Service.isConnected();
        return ResponseEntity.ok(Map.of(
            "s3",     s3Ok ? "connected" : "disconnected",
            "status", s3Ok ? "UP" : "DOWN"
        ));
    }
}