package com.news.apnews.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "cat_state")
public class State {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String imageUrl;
    private String date;
}