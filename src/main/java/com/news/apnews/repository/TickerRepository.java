package com.news.apnews.repository;

import com.news.apnews.model.Ticker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TickerRepository extends JpaRepository<Ticker, Long> {

    // ERROR WAS HERE: findByIsActiveTrueOrderByPriorityDesc
    // After renaming the field from 'isActive' to 'active' in Ticker.java,
    // the JPA method name must also change to 'findByActiveTrueOrderByPriorityDesc'
    List<Ticker> findByActiveTrueOrderByPriorityDesc();
}