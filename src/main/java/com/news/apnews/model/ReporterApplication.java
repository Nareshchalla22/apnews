package com.news.apnews.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "reporter_applications")
@Data
public class ReporterApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Personal Info
    private String fullName;
    private String email;
    private String phone;
    private String city;
    private String state;
    private String designation;
    private String experience;
    private String aadharNumber;

    @Column(columnDefinition = "TEXT")
    private String about;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String photoUrl;

    // Plan / Payment
    private String planId;
    private String planName;
    private Integer amount;
    private String txnId;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String paymentProof;   // Base64 screenshot

    // Status: PENDING | APPROVED | REJECTED
    @Column(nullable = false)
    private String status = "PENDING";

    private String adminNote;      // rejection/approval message from admin

    private LocalDateTime appliedAt = LocalDateTime.now();
    private LocalDateTime reviewedAt;
}