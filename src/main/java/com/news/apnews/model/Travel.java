package com.news.apnews.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "cat_travel")
public class Travel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(columnDefinition = "TEXT")
    private String imageUrl;

    // JSON array of image URLs, e.g. ["https://...a.jpg","https://...b.jpg"].
    // imageUrl stays as the cover (first) image for backward compatibility.
    @Column(columnDefinition = "TEXT")
    private String imageUrls;
    private String date;
}