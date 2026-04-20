package com.news.apnews.controller;

import com.news.apnews.model.Ticker;
import com.news.apnews.repository.TickerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ticker")
@CrossOrigin(origins = {"https://flash-news-ui.vercel.app", "http://localhost:5173"})
public class TickerController {

    @Autowired
    private TickerRepository repo;

    @GetMapping("/all")
    public List<Ticker> getAll() {
        return repo.findAll();
    }

    @PostMapping("/create")
    public Ticker create(@RequestBody Ticker ticker) {
        // Set default if not provided
        if (ticker.getPriority() == null) ticker.setPriority("High");
        return repo.save(ticker);
    }

    @PutMapping("/update/{id}")
    public Ticker update(@PathVariable Long id, @RequestBody Ticker updated) {
        Ticker t = repo.findById(id).orElseThrow(() -> new RuntimeException("Ticker not found"));
        t.setMessage(updated.getMessage());
        t.setActive(updated.isActive());
        return repo.save(t);
    }
}