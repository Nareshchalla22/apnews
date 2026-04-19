package com.news.apnews.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.news.apnews.model.Ticker;
import com.news.apnews.repository.TickerRepository;

@RestController
//@RequestMapping("/api")
//@CrossOrigin(origins = "http://localhost:5173")
@CrossOrigin(origins = "https://flash-news-ui.vercel.app/") // Allow React frontend
public class TickerController {

    @Autowired
    private TickerRepository tickerRepository;

    // 1. Fetch all (Matches newsService.getTicker)
    @GetMapping("/all")
    public List<Ticker> getAllMessages() {
        return tickerRepository.findAll();
    }

    // 2. Create (Matches newsService.createTicker)
    @PostMapping("/create")
    public ResponseEntity<Ticker> createTicker(@RequestBody Ticker ticker) {
        // Defaulting new tickers to active if not specified
        if (ticker.getPriority() == null) ticker.setPriority("Medium");
        Ticker saved = tickerRepository.save(ticker);
        return ResponseEntity.ok(saved);
    }

    // 3. Update (Matches newsService.updateTicker)
    @PutMapping("/update/{id}")
    public ResponseEntity<Ticker> updateTicker(@PathVariable Long id, @RequestBody Ticker tickerDetails) {
        Ticker ticker = tickerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ticker not found with id: " + id));

        ticker.setMessage(tickerDetails.getMessage());
        ticker.setActive(tickerDetails.isActive());
        ticker.setPriority(tickerDetails.getPriority());

        Ticker updatedTicker = tickerRepository.save(ticker);
        return ResponseEntity.ok(updatedTicker);
    }
}