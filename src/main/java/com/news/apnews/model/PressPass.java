package com.news.apnews.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "cat_press_pass")
@Data
public class PressPass {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Core identity
    private String reporterName;
    private String employeeId;
    private String designation;   // Senior Reporter, Anchor, etc.
    private String department;    // News Division, Sports Desk, etc.

    // Medical & contact
    private String bloodGroup;
    private String contactNumber;

    // Validity
    private String validUntil;    // e.g. "2026"

    // Photo stored as Base64 text or URL
    @Lob
    @Column(columnDefinition = "TEXT")
    private String photoUrl;
}