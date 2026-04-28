package com.news.apnews.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "cat_ticker")
@Data
@JsonIgnoreProperties(ignoreUnknown = true) 
public class Ticker {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String message;

    // FIX: Use Boolean (wrapper class) instead of boolean (primitive)
    // Boolean can hold null values, boolean cannot
    @Column(name = "active", columnDefinition = "boolean default false")
    private Boolean active = false;

    private String priority;

    // Helper method — returns false if null
    public boolean isActive() {
        return Boolean.TRUE.equals(active);
    }
}