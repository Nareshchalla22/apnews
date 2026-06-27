package com.news.apnews.controller;

import com.news.apnews.model.Advertisement;
import com.news.apnews.repository.AdvertisementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ads")
public class AdvertisementController {

    @Autowired
    private AdvertisementRepository adRepo;

    // ── GET ALL (admin) ───────────────────────────────────────────────────
    @GetMapping
    public ResponseEntity<List<Advertisement>> getAll() {
        return ResponseEntity.ok(adRepo.findAll());
    }

    // ── GET ACTIVE (public — for homepage display) ────────────────────────
    @GetMapping("/active")
    @Transactional(readOnly = true)
    public ResponseEntity<List<Advertisement>> getActive() {
        System.out.println("Fetching active and scheduled ads at " + LocalDateTime.now());
        return ResponseEntity.ok(adRepo.findActiveAndScheduled(LocalDateTime.now()));
    }

    // ── GET BY PLACEMENT (public) ─────────────────────────────────────────
    // placement: top | middle | bottom | sidebar | all
    @GetMapping("/placement/{placement}")
    public ResponseEntity<List<Advertisement>> getByPlacement(@PathVariable String placement) {
        return ResponseEntity.ok(adRepo.findActiveByPlacement(placement));
    }

    // ── GET BY TYPE (public) ──────────────────────────────────────────────
    // type: school | college | shopping | business | other
    @GetMapping("/type/{type}")
    public ResponseEntity<List<Advertisement>> getByType(@PathVariable String type) {
        return ResponseEntity.ok(adRepo.findByActiveTrueAndTypeOrderByPriorityAsc(type));
    }

    // ── GET SINGLE ────────────────────────────────────────────────────────
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        return adRepo.findById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    // ── CREATE ────────────────────────────────────────────────────────────
    @PostMapping
    public ResponseEntity<?> create(@RequestBody Advertisement ad) {
        // Set defaults
        if (ad.getActive()   == null) ad.setActive(true);
        if (ad.getPriority() == null) ad.setPriority(5);
        if (ad.getPlacement()== null) ad.setPlacement("all");
        if (ad.getBgColor()  == null) ad.setBgColor("#0a1628");
        if (ad.getAccentColor()==null)ad.setAccentColor("#3b82f6");

        // Auto-set badge if not provided
        if (ad.getBadge() == null || ad.getBadge().isBlank()) {
            ad.setBadge(switch (ad.getType() != null ? ad.getType() : "other") {
                case "school"   -> "🏫 School";
                case "college"  -> "🎓 College";
                case "shopping" -> "🛍️ Shopping";
                case "business" -> "💼 Business";
                default         -> "📢 Ad";
            });
        }

        Advertisement saved = adRepo.save(ad);
        return ResponseEntity.ok(saved);
    }

    // ── UPDATE ────────────────────────────────────────────────────────────
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Advertisement updated) {
        return adRepo.findById(id).map(ad -> {
            if (updated.getTitle()      != null) ad.setTitle(updated.getTitle());
            if (updated.getSubtitle()   != null) ad.setSubtitle(updated.getSubtitle());
            if (updated.getPhone()      != null) ad.setPhone(updated.getPhone());
            if (updated.getUrl()        != null) ad.setUrl(updated.getUrl());
            if (updated.getType()       != null) ad.setType(updated.getType());
            if (updated.getBadge()      != null) ad.setBadge(updated.getBadge());
            if (updated.getTag()        != null) ad.setTag(updated.getTag());
            if (updated.getAccentColor()!= null) ad.setAccentColor(updated.getAccentColor());
            if (updated.getBgColor()    != null) ad.setBgColor(updated.getBgColor());
            if (updated.getImageUrl()   != null) ad.setImageUrl(updated.getImageUrl());
            if (updated.getPlacement()  != null) ad.setPlacement(updated.getPlacement());
            if (updated.getPriority()   != null) ad.setPriority(updated.getPriority());
            if (updated.getActive()     != null) ad.setActive(updated.getActive());
            if (updated.getStartDate()  != null) ad.setStartDate(updated.getStartDate());
            if (updated.getEndDate()    != null) ad.setEndDate(updated.getEndDate());
            return ResponseEntity.ok(adRepo.save(ad));
        }).orElse(ResponseEntity.notFound().build());
    }

    // ── TOGGLE ACTIVE ─────────────────────────────────────────────────────
    @PatchMapping("/{id}/toggle")
    public ResponseEntity<?> toggle(@PathVariable Long id) {
        return adRepo.findById(id).map(ad -> {
            ad.setActive(!ad.getActive());
            adRepo.save(ad);
            return ResponseEntity.ok(Map.of(
                "id",      ad.getId(),
                "active",  ad.getActive(),
                "message", ad.getActive() ? "Ad activated" : "Ad deactivated"
            ));
        }).orElse(ResponseEntity.notFound().build());
    }

    // ── DELETE ────────────────────────────────────────────────────────────
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        if (!adRepo.existsById(id)) return ResponseEntity.notFound().build();
        adRepo.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "Ad deleted successfully"));
    }

    // ── TRACK IMPRESSION (called when ad is shown) ────────────────────────
    @PostMapping("/{id}/impression")
    public ResponseEntity<?> trackImpression(@PathVariable Long id) {
        if (!adRepo.existsById(id)) return ResponseEntity.notFound().build();
        adRepo.incrementImpressions(id);
        return ResponseEntity.ok(Map.of("tracked", true));
    }

    // ── TRACK CLICK (called when ad is clicked) ───────────────────────────
    @PostMapping("/{id}/click")
    public ResponseEntity<?> trackClick(@PathVariable Long id) {
        if (!adRepo.existsById(id)) return ResponseEntity.notFound().build();
        adRepo.incrementClicks(id);
        return ResponseEntity.ok(Map.of("tracked", true));
    }

    // ── STATS SUMMARY (admin) ─────────────────────────────────────────────
    @GetMapping("/stats")
    public ResponseEntity<?> stats() {
        List<Advertisement> all = adRepo.findAll();
        long totalAds       = all.size();
        long activeAds      = all.stream().filter(a -> Boolean.TRUE.equals(a.getActive())).count();
        long totalImpress   = all.stream().mapToLong(a -> a.getImpressions() != null ? a.getImpressions() : 0).sum();
        long totalClicks    = all.stream().mapToLong(a -> a.getClicks()      != null ? a.getClicks()      : 0).sum();
        double ctr          = totalImpress > 0 ? (double) totalClicks / totalImpress * 100 : 0;

        return ResponseEntity.ok(Map.of(
            "totalAds",        totalAds,
            "activeAds",       activeAds,
            "inactiveAds",     totalAds - activeAds,
            "totalImpressions",totalImpress,
            "totalClicks",     totalClicks,
            "ctr",             String.format("%.2f%%", ctr)
        ));
    }
}