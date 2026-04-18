package com.news.apnews.repository;

import com.news.apnews.model.Ticker;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TickerRepository extends JpaRepository<Ticker, Long> {
    // Only get messages that are turned ON, sorted by priority
    List<Ticker> findByIsActiveTrueOrderByPriorityDesc();
}