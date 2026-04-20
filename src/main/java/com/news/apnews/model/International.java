package com.news.apnews.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "cat_international")
@Data
public class International {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String country;
    private String headline;
    @Column(columnDefinition = "TEXT")
    private String globalReport;
    @Lob
    @Column(columnDefinition = "TEXT")
    private String imageUrl;
    private String sourceAgency; // e.g., Reuters, AP
}