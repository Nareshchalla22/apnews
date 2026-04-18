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
    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String imageUrl;
}