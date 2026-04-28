package com.news.apnews.controller;

import com.news.apnews.model.Ticker;
import com.news.apnews.repository.TickerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class TickerController {

    @Autowired
    private TickerRepository repo;

    // ── GET ALL ───────────────────────────────────────────────────────
    @GetMapping("/all")
    public ResponseEntity<List<Ticker>> getAll() {
        return ResponseEntity.ok(repo.findAll());
    }

    // ── GET ACTIVE ONLY ───────────────────────────────────────────────
    @GetMapping("/all/active")
    public ResponseEntity<List<Ticker>> getActive() {
        return ResponseEntity.ok(repo.findByActiveTrueOrderByPriorityDesc());
    }

    // ── GET BY ID ─────────────────────────────────────────────────────
    @GetMapping("/ticker/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        return repo.findById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    // ── CREATE ────────────────────────────────────────────────────────
    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody Ticker ticker) {
        try {
            if (ticker.getMessage() == null || ticker.getMessage().isBlank()) {
                return ResponseEntity.badRequest()
                    .body(Map.of("message", "Ticker message is required"));
            }
            if (ticker.getPriority() == null || ticker.getPriority().isBlank()) {
                ticker.setPriority("High");
            }
            return ResponseEntity.ok(repo.save(ticker));

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(Map.of("message", "Creation failed: " + e.getMessage()));
        }
    }

    // ── UPDATE ────────────────────────────────────────────────────────
    @PutMapping("/update/{id}")
    public ResponseEntity<?> update(@PathVariable Long id,
                                    @RequestBody Ticker updated) {
        try {
            Ticker t = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Ticker not found"));

            if (updated.getMessage() != null) {
                t.setMessage(updated.getMessage());
            }
            if (updated.getPriority() != null) {
                t.setPriority(updated.getPriority());
            }
            t.setActive(updated.isActive());

            return ResponseEntity.ok(repo.save(t));

        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(Map.of("message", "Update failed: " + e.getMessage()));
        }
    }

    // ── TOGGLE ACTIVE/INACTIVE ────────────────────────────────────────
    @PatchMapping("/ticker/{id}/toggle")
    public ResponseEntity<?> toggle(@PathVariable Long id) {
        try {
            Ticker t = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Ticker not found"));

            t.setActive(!t.isActive());
            Ticker saved = repo.save(t);

            return ResponseEntity.ok(Map.of(
                "id",      saved.getId(),
                "active",  saved.isActive(),
                "message", saved.isActive() ? "Ticker activated" : "Ticker deactivated"
            ));

        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(Map.of("message", e.getMessage()));
        }
    }

    // ── DELETE ────────────────────────────────────────────────────────
    @DeleteMapping("/ticker/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            if (!repo.existsById(id)) {
                return ResponseEntity.notFound().build();
            }
            repo.deleteById(id);
            return ResponseEntity.ok(Map.of(
                "message", "Ticker deleted successfully",
                "id",      id
            ));

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(Map.of("message", "Delete failed: " + e.getMessage()));
        }
    }
}