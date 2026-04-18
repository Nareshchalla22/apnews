package com.news.apnews.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "cat_press_pass")
@Data
public class PressPass {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String reporterName;
    private String employeeId;
    private String bloodGroup;
    private String validUntil;
    private String contactNumber;
    private String photoUrl;
}