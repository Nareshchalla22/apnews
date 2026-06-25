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
    private String password;       // BCrypt encoded

    @Column(nullable = false)
    private String role;           // ADMIN | REPORTER | EDITOR | VIEWER

    private boolean enabled = true;

    // ── Reporter profile fields ───────────────────────────────────────────
    // Added for reporter ID card, dashboard display, and press credentials.
    // These columns are added via spring.jpa.hibernate.ddl-auto=update
    // so no manual migration needed.

    private String fullName;       // Reporter's real name
    private String email;          // Contact email
    private String phone;          // Contact phone

    private String planId;         // basic | pro | elite
    private String planName;       // Field Reporter | Senior Correspondent | Bureau Correspondent

    @Lob
    @Column(columnDefinition = "TEXT")
    private String photoUrl;       // Profile photo — S3 URL or base64
}