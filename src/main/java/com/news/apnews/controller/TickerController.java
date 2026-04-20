package com.news.apnews.controller;

import com.news.apnews.model.Ticker;
import com.news.apnews.repository.TickerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ticker")
@CrossOrigin(origins = {"https://flash-news-ui.vercel.app", "http://localhost:5173"})
public class TickerController {

    private static final Logger logger = LoggerFactory.getLogger(TickerController.class);

    @Autowired
    private TickerRepository repo;

    @GetMapping("/all")
    public List<Ticker> getAll() {
        logger.info("Fetching all tickers from satellite uplink...");
        return repo.findAll();
    }

    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody Ticker ticker) {
        try {
            if (ticker.getPriority() == null) ticker.setPriority("High");
            Ticker saved = repo.save(ticker);
            logger.info("Ticker created successfully: {}", saved.getId());
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            logger.error("FAILED to create ticker: {}", e.getMessage());
            return ResponseEntity.internalServerError().body("Creation Error: " + e.getMessage());
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Ticker updated) {
        try {
            Ticker t = repo.findById(id).orElseThrow(() -> {
                logger.warn("Ticker ID {} not found for update", id);
                return new RuntimeException("Ticker not found");
            });
            
            t.setMessage(updated.getMessage());
            t.setActive(updated.isActive());
            
            Ticker saved = repo.save(t);
            logger.info("Ticker ID {} updated successfully", id);
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            logger.error("CRITICAL: Error updating Ticker ID {}: {}", id, e.getMessage());
            return ResponseEntity.internalServerError().body("Update Error: " + e.getMessage());
        }
    }
}