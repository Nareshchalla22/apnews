package com.news.apnews.controller;

import com.news.apnews.model.NewsItem;
import com.news.apnews.repository.NewsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Base64;

@RestController
@RequestMapping("/api/meta")
public class MetaImageController {

    @Autowired
    private NewsRepository newsRepository;

    @GetMapping("/image/{cat}/{id}")
    public ResponseEntity<byte[]> getArticleImage(
            @PathVariable String cat,
            @PathVariable String id) {

        try {
            Long newsId = Long.parseLong(id);
            NewsItem item = newsRepository.findById(newsId).orElse(null);

            if (item != null && item.getImageUrl() != null && item.getImageUrl().startsWith("data:image")) {
                String base64Data = item.getImageUrl();

                // Extract content type (e.g. image/jpeg)
                String contentType = base64Data.substring(5, base64Data.indexOf(";"));

                // Extract actual base64 string after comma
                String base64Image = base64Data.substring(base64Data.indexOf(",") + 1);

                byte[] imageBytes = Base64.getDecoder().decode(base64Image);

                return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_TYPE, contentType)
                    .header(HttpHeaders.CACHE_CONTROL, "public, max-age=86400")
                    .body(imageBytes);
            }
        } catch (Exception e) {
            // fall through to default
        }

        // Return default fallback image (1x1 transparent or your logo)
        return ResponseEntity.notFound().build();
    }
}