package com.news.apnews.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "cat_entertainment")
@Data
public class Entertainment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String movieTitle;
    private String celebrityName;
    @Column(columnDefinition = "TEXT")
    private String gossipContent;
}