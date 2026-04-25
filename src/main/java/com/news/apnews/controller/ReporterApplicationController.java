package com.news.apnews.controller;

import com.news.apnews.model.ReporterApplication;
import com.news.apnews.repository.ReporterApplicationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reporter-application")
public class ReporterApplicationController {

    @Autowired
    private ReporterApplicationRepository repo;

    // ── PUBLIC: Submit new application ────────────────────────────────────────
    @PostMapping
    public ResponseEntity<?> submit(@RequestBody ReporterApplication app) {
        // Validation
        if (app.getFullName() == null || app.getFullName().isBlank())
            return ResponseEntity.badRequest().body(Map.of("message", "Full name required"));
        if (app.getEmail() == null || app.getEmail().isBlank())
            return ResponseEntity.badRequest().body(Map.of("message", "Email required"));
        if (app.getTxnId() == null || app.getTxnId().isBlank())
            return ResponseEntity.badRequest().body(Map.of("message", "Transaction ID required"));

        // Prevent duplicate transactions
        if (repo.existsByTxnId(app.getTxnId()))
            return ResponseEntity.badRequest().body(Map.of("message", "Transaction already submitted"));

        app.setStatus("PENDING");
        app.setAppliedAt(LocalDateTime.now());

        ReporterApplication saved = repo.save(app);
        return ResponseEntity.ok(Map.of(
            "id",      saved.getId(),
            "message", "Application submitted successfully",
            "status",  saved.getStatus()
        ));
    }

    // ── ADMIN: Get all applications ───────────────────────────────────────────
    @GetMapping
    public List<ReporterApplication> getAll() {
        return repo.findAll();
    }

    // ── ADMIN: Get by status ──────────────────────────────────────────────────
    @GetMapping("/status/{status}")
    public List<ReporterApplication> getByStatus(@PathVariable String status) {
        return repo.findByStatus(status.toUpperCase());
    }

    // ── ADMIN: Get single ─────────────────────────────────────────────────────
    @GetMapping("/{id}")
    public ResponseEntity<ReporterApplication> getById(@PathVariable Long id) {
        return repo.findById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    // ── ADMIN: Approve application ────────────────────────────────────────────
    @PutMapping("/{id}/approve")
    public ResponseEntity<?> approve(@PathVariable Long id,
                                      @RequestBody(required = false) Map<String, String> body) {
        return repo.findById(id).map(app -> {
            app.setStatus("APPROVED");
            app.setReviewedAt(LocalDateTime.now());
            if (body != null && body.containsKey("note"))
                app.setAdminNote(body.get("note"));
            repo.save(app);
            return ResponseEntity.ok(Map.of(
                "id",      app.getId(),
                "message", "Application approved",
                "status",  "APPROVED"
            ));
        }).orElse(ResponseEntity.notFound().build());
    }

    // ── ADMIN: Reject application ─────────────────────────────────────────────
    @PutMapping("/{id}/reject")
    public ResponseEntity<?> reject(@PathVariable Long id,
                                     @RequestBody(required = false) Map<String, String> body) {
        return repo.findById(id).map(app -> {
            app.setStatus("REJECTED");
            app.setReviewedAt(LocalDateTime.now());
            if (body != null && body.containsKey("note"))
                app.setAdminNote(body.get("note"));
            repo.save(app);
            return ResponseEntity.ok(Map.of(
                "id",      app.getId(),
                "message", "Application rejected",
                "status",  "REJECTED"
            ));
        }).orElse(ResponseEntity.notFound().build());
    }

    // ── ADMIN: Delete application ─────────────────────────────────────────────
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        if (!repo.existsById(id))
            return ResponseEntity.notFound().build();
        repo.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "Deleted", "id", id));
    }
}