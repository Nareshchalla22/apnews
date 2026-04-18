package com.news.apnews.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "nav_menu")
@Data
@Getter
@Setter
public class CategoryMenu {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String label;
    private String path;
    private String iconName;
}