package com.news.apnews.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "advertisements")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Advertisement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ── Basic Info ────────────────────────────────────────────────────────
    @Column(nullable = false)
    private String title;           // Business name

    @Column(columnDefinition = "TEXT")
    private String subtitle;        // Tagline / description

    private String phone;           // Contact number
    private String url;             // Website / landing page

    // ── Type & Category ───────────────────────────────────────────────────
    @Column(nullable = false)
    private String type;            // school | college | shopping | business | other

    private String badge;           // "🏫 School" | "🎓 College" | "🛍️ Shopping" etc
    private String tag;             // "Admissions Open" | "Grand Opening" | "Sponsored"

    // ── Appearance ────────────────────────────────────────────────────────
    private String accentColor;     // hex color e.g. #3b82f6
    private String bgColor;         // hex color e.g. #0a1628

    @Column(columnDefinition = "TEXT")
    private String imageUrl;        // S3 URL or base64

    // ── Placement ─────────────────────────────────────────────────────────
    private String placement;       // top | middle | bottom | sidebar | all
    private Integer priority;       // 1=highest, lower = shown first
    private Boolean active;         // true = show, false = hide

    // ── Schedule ──────────────────────────────────────────────────────────
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    // ── Stats ─────────────────────────────────────────────────────────────
    private Long impressions = 0L;  // how many times shown
    private Long clicks      = 0L;  // how many times clicked

    // ── Audit ─────────────────────────────────────────────────────────────
    @Column(updatable = false)
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (active      == null) active      = true;
        if (priority    == null) priority    = 5;
        if (impressions == null) impressions = 0L;
        if (clicks      == null) clicks      = 0L;
        if (placement   == null) placement   = "all";
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}