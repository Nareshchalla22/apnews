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
    @Lob
    @Column(columnDefinition = "TEXT")
    private String imageUrl;
    private String date;
}