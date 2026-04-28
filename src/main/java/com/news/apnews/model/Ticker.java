package com.news.apnews.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "cat_ticker")
@Data
public class Ticker {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String message;

    // ERROR WAS HERE: Field was named 'isActive' (boolean).
    // Lombok generates isActive() getter for boolean fields named 'isActive'
    // but JPA query 'findByIsActiveTrueOrderByPriorityDesc' cannot resolve it.
    // FIX: rename to 'active' — Lombok generates isActive() getter automatically
    // and JPA query 'findByActiveTrueOrderByPriorityDesc' works correctly.
    private boolean active;

    private String priority; // High, Medium, Low

    // Convenience method so existing code calling isActive() still works
    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}