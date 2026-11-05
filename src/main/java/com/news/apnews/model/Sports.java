package com.news.apnews.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "cat_sports")
@Data
public class Sports {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String matchTitle;

    @Column(columnDefinition = "TEXT")
    private String summary;

    private String scoreUpdate;

    
    @Column(columnDefinition = "TEXT")
    private String imageUrl;

    // JSON array of image URLs, e.g. ["https://...a.jpg","https://...b.jpg"].
    // imageUrl stays as the cover (first) image for backward compatibility.
    @Column(columnDefinition = "TEXT")
    private String imageUrls;
}