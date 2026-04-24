package com.news.apnews.controller;

import com.news.apnews.model.PressPass;
import com.news.apnews.repository.PressPassRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/press-pass")
public class PressPassController {

    @Autowired
    private PressPassRepository repo;

    // ── GET ALL ──────────────────────────────────────────────────────────────
    @GetMapping
    public List<PressPass> getAll() {
        return repo.findAll();
    }

    // ── GET SINGLE ───────────────────────────────────────────────────────────
    @GetMapping("/{id}")
    public ResponseEntity<PressPass> getById(@PathVariable Long id) {
        return repo.findById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    // ── CREATE ───────────────────────────────────────────────────────────────
    @PostMapping
    public ResponseEntity<?> create(@RequestBody PressPass pass) {
        if (pass.getReporterName() == null || pass.getReporterName().isBlank()) {
            return ResponseEntity.badRequest()
                .body(Map.of("message", "Reporter name is required"));
        }
        PressPass saved = repo.save(pass);
        return ResponseEntity.ok(saved);
    }

    // ── UPDATE ───────────────────────────────────────────────────────────────
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody PressPass updated) {
        return repo.findById(id).map(existing -> {
            existing.setReporterName(updated.getReporterName());
            existing.setEmployeeId(updated.getEmployeeId());
            existing.setBloodGroup(updated.getBloodGroup());
            existing.setValidUntil(updated.getValidUntil());
            existing.setContactNumber(updated.getContactNumber());
            existing.setPhotoUrl(updated.getPhotoUrl());

            // New fields — update PressPass model to include these
            if (updated.getDesignation() != null)
                existing.setDesignation(updated.getDesignation());
            if (updated.getDepartment() != null)
                existing.setDepartment(updated.getDepartment());

            return ResponseEntity.ok(repo.save(existing));
        }).orElse(ResponseEntity.notFound().build());
    }

    // ── DELETE ───────────────────────────────────────────────────────────────
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        if (!repo.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        repo.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "Deleted successfully", "id", id));
    }
}