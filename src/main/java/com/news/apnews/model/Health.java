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
@Table(name = "cat_health")
@Data
public class Health {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String topic; // e.g., COVID-19, Nutrition
    private String title;
    @Column(columnDefinition = "TEXT")
    private String medicalAdvice;
    private String doctorConsultant;
    @Lob
    @Column(columnDefinition = "TEXT")
    private String imageUrl;
}