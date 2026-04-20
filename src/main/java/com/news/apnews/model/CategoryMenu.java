package com.news.apnews.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "nav_menu")
@Data
public class CategoryMenu {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String label;
    private String path;
    private String iconName;
}