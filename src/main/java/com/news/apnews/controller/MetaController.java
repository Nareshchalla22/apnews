package com.news.apnews.controller;

import com.news.apnews.model.*;
import com.news.apnews.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Base64;
import java.util.Optional;

/**
 * Share-preview pages for WhatsApp / Facebook / Telegram / X / LinkedIn.
 * Vercel sends crawler requests for /category/{cat}/{id} here (see vercel.json).
 * Replaces BOTH old MetaController and MetaImageController (delete MetaImageController.java).
 */
@RestController
@RequestMapping("/api/meta")
public class MetaController {

    private static final String SITE = "https://ap13news.in";
    private static final String API  = "https://api.ap13news.in";
    private static final String DEFAULT_IMAGE = SITE + "/og-default.jpg";

    @Autowired private GlobalRepository globalRepo;
    @Autowired private NationalRepository nationalRepo;
    @Autowired private StateRepository stateRepo;
    @Autowired private BusinessRepository businessRepo;
    @Autowired private CrimeRepository crimeRepo;
    @Autowired private EntertainmentRepository entertainmentRepo;
    @Autowired private SportsRepository sportsRepo;
    @Autowired private HealthRepository healthRepo;
    @Autowired private PoliticsRepository politicsRepo;
    @Autowired private TravelRepository travelRepo;
    @Autowired private TechnologyRepository technologyRepo;

    private record Info(String title, String desc, String image) {}

    private Optional<Info> find(String cat, Long id) {
        switch (cat.toLowerCase()) {
            case "global":        return globalRepo.findById(id).map(n -> new Info(n.getTitle(), n.getDescription(), n.getImageUrl()));
            case "national":      return nationalRepo.findById(id).map(n -> new Info(n.getTitle(), n.getDescription(), n.getImageUrl()));
            case "state":         return stateRepo.findById(id).map(n -> new Info(n.getTitle(), n.getDescription(), n.getImageUrl()));
            case "business":      return businessRepo.findById(id).map(n -> new Info(n.getHeadline(), n.getAnalysis(), n.getImageUrl()));
            case "crime":         return crimeRepo.findById(id).map(n -> new Info(n.getTitle(), n.getDescription(), n.getImageUrl()));
            case "entertainment": return entertainmentRepo.findById(id).map(n -> new Info(n.getMovieTitle(), n.getGossipContent(), n.getImageUrl()));
            case "sports":        return sportsRepo.findById(id).map(n -> new Info(n.getMatchTitle(), n.getSummary(), n.getImageUrl()));
            case "health":        return healthRepo.findById(id).map(n -> new Info(n.getTitle(), n.getMedicalAdvice(), n.getImageUrl()));
            case "politics":      return politicsRepo.findById(id).map(n -> new Info(n.getTitle(), n.getDescription(), n.getImageUrl()));
            case "travel":        return travelRepo.findById(id).map(n -> new Info(n.getTitle(), n.getDescription(), n.getImageUrl()));
            case "technology":    return technologyRepo.findById(id).map(n -> new Info(n.getGadgetHead(), n.getTechReview(), n.getImageUrl()));
            default:              return Optional.empty();
        }
    }

    private static String esc(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
                .replace("\"", "&quot;").replace("\r", " ").replace("\n", " ").trim();
    }

    /** Cloudinary: crop to the 1200x630 share size, compress, force JPG (WhatsApp-friendly). */
    private static String optimize(String url) {
        if (url.contains("res.cloudinary.com") && url.contains("/upload/") && !url.contains("/upload/c_")) {
            return url.replace("/upload/", "/upload/c_fill,w_1200,h_630,g_auto,q_auto,f_jpg/");
        }
        return url;
    }

    private static String shareImage(String cat, String id, String img) {
        if (img == null || img.isBlank()) return DEFAULT_IMAGE;
        if (img.startsWith("data:image")) return API + "/api/meta/image/" + cat + "/" + id; // old base64 posts
        if (img.startsWith("http")) return optimize(img);
        return DEFAULT_IMAGE;
    }

    @GetMapping("/category/{cat}/{id}")
    public ResponseEntity<String> page(@PathVariable String cat, @PathVariable String id) {
        String safeCat = cat.replaceAll("[^a-zA-Z]", "").toLowerCase();
        String safeId  = id.replaceAll("[^0-9]", "");

        String title = "AP13 News";
        String desc  = "Latest breaking news from Andhra Pradesh & Telangana - AP13 News";
        String image = DEFAULT_IMAGE;

        try {
            Optional<Info> found = find(safeCat, Long.parseLong(safeId));
            if (found.isPresent()) {
                Info n = found.get();
                if (n.title() != null && !n.title().isBlank()) title = n.title();
                if (n.desc() != null && !n.desc().isBlank()) {
                    desc = n.desc().length() > 200 ? n.desc().substring(0, 200) + "..." : n.desc();
                }
                image = shareImage(safeCat, safeId, n.image());
            }
        } catch (Exception ignored) { /* keep defaults */ }

        String url = SITE + "/category/" + safeCat + "/" + safeId;
        String t = esc(title), d = esc(desc), i = esc(image), u = esc(url);

        String html = "<!DOCTYPE html>\n<html lang=\"te\">\n<head>\n<meta charset=\"UTF-8\"/>\n"
            + "<title>" + t + " — AP13 News</title>\n"
            + "<meta name=\"description\" content=\"" + d + "\"/>\n"
            + "<meta property=\"og:title\" content=\"" + t + "\"/>\n"
            + "<meta property=\"og:description\" content=\"" + d + "\"/>\n"
            + "<meta property=\"og:image\" content=\"" + i + "\"/>\n"
            + "<meta property=\"og:image:secure_url\" content=\"" + i + "\"/>\n"
            + "<meta property=\"og:image:width\" content=\"1200\"/>\n"
            + "<meta property=\"og:image:height\" content=\"630\"/>\n"
            + "<meta property=\"og:url\" content=\"" + u + "\"/>\n"
            + "<meta property=\"og:type\" content=\"article\"/>\n"
            + "<meta property=\"og:site_name\" content=\"AP13 News\"/>\n"
            + "<meta name=\"twitter:card\" content=\"summary_large_image\"/>\n"
            + "<meta name=\"twitter:title\" content=\"" + t + "\"/>\n"
            + "<meta name=\"twitter:description\" content=\"" + d + "\"/>\n"
            + "<meta name=\"twitter:image\" content=\"" + i + "\"/>\n"
            + "<link rel=\"canonical\" href=\"" + u + "\"/>\n"
            + "<meta http-equiv=\"refresh\" content=\"0; url=" + u + "\"/>\n"
            + "</head>\n<body><a href=\"" + u + "\">" + t + "</a></body>\n</html>";

        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_HTML_VALUE + ";charset=UTF-8")
            .header(HttpHeaders.CACHE_CONTROL, "public, max-age=300")
            .body(html);
    }

    /** Serves old posts whose image was saved as base64 inside the database. */
    @GetMapping("/image/{cat}/{id}")
    public ResponseEntity<byte[]> image(@PathVariable String cat, @PathVariable String id) {
        try {
            Optional<Info> found = find(cat.replaceAll("[^a-zA-Z]", ""), Long.parseLong(id.replaceAll("[^0-9]", "")));
            if (found.isPresent() && found.get().image() != null && found.get().image().startsWith("data:image")) {
                String data = found.get().image();
                String contentType = data.substring(5, data.indexOf(";"));
                byte[] bytes = Base64.getDecoder().decode(data.substring(data.indexOf(",") + 1));
                return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_TYPE, contentType)
                    .header(HttpHeaders.CACHE_CONTROL, "public, max-age=86400")
                    .body(bytes);
            }
        } catch (Exception ignored) { /* fall through */ }
        return ResponseEntity.notFound().build();
    }
}