package com.news.apnews.controller;

import com.news.apnews.model.NewsItem; // adjust to your entity
import com.news.apnews.repository.NewsRepository; // adjust to your repo
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/meta")
public class MetaController {

    @Autowired
    private NewsRepository newsRepository; // adjust to your repo

    @GetMapping("/category/{cat}/{id}")
    public ResponseEntity<String> getMeta(
            @PathVariable String cat,
            @PathVariable String id) {

        String title = "AP13 News";
        String desc  = "Latest breaking news from Andhra Pradesh & Telangana - AP13 News";
        String url   = "https://ap13news.in/category/" + cat + "/" + id;
        String image = "https://ap13news.in/og-default.jpg"; // fallback

        try {
            Long newsId = Long.parseLong(id);
            NewsItem item = newsRepository.findById(newsId).orElse(null);

            if (item != null) {
                title = item.getTitle() != null ? item.getTitle() : title;
                desc  = item.getDescription() != null
                        ? (item.getDescription().length() > 200
                            ? item.getDescription().substring(0, 200) + "..."
                            : item.getDescription())
                        : desc;

                // Use S3 URL directly — already a real https:// URL
                if (item.getImageUrl() != null && item.getImageUrl().startsWith("http")) {
                    image = item.getImageUrl();
                }
            }
        } catch (Exception e) {
            // fallback values stay
        }

        title = title.replace("\"", "&quot;");
        desc  = desc.replace("\"", "&quot;");

        String html = "<!DOCTYPE html>\n" +
            "<html>\n" +
            "<head>\n" +
            "<meta charset=\"UTF-8\"/>\n" +
            "<title>" + title + "</title>\n" +
            "<meta property=\"og:title\" content=\"" + title + "\"/>\n" +
            "<meta property=\"og:description\" content=\"" + desc + "\"/>\n" +
            "<meta property=\"og:image\" content=\"" + image + "\"/>\n" +
            "<meta property=\"og:image:secure_url\" content=\"" + image + "\"/>\n" +
            "<meta property=\"og:image:width\" content=\"1200\"/>\n" +
            "<meta property=\"og:image:height\" content=\"630\"/>\n" +
            "<meta property=\"og:url\" content=\"" + url + "\"/>\n" +
            "<meta property=\"og:type\" content=\"article\"/>\n" +
            "<meta property=\"og:site_name\" content=\"AP13 News\"/>\n" +
            "<meta name=\"twitter:card\" content=\"summary_large_image\"/>\n" +
            "<meta name=\"twitter:title\" content=\"" + title + "\"/>\n" +
            "<meta name=\"twitter:description\" content=\"" + desc + "\"/>\n" +
            "<meta name=\"twitter:image\" content=\"" + image + "\"/>\n" +
            "<meta http-equiv=\"refresh\" content=\"0; url=" + url + "\"/>\n" +
            "</head>\n" +
            "<body></body>\n" +
            "</html>";

        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_HTML_VALUE + ";charset=UTF-8")
            .body(html);
    }
}