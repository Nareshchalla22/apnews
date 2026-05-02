package com.news.apnews.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "app_users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;   // BCrypt encoded

    @Column(nullable = false)
    private String role;       // ADMIN | REPORTER | EDITOR | VIEWER

    private boolean enabled = true;

    // Reporter-specific fields (populated when created from approved application)
    private String fullName;
    private String email;
    private String phone;
    private String planId;      // basic | pro | elite
    private String planName;    // Field Reporter | Senior Correspondent | Bureau Correspondent
}