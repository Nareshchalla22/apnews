package com.news.apnews.repository;

import com.news.apnews.model.Ticker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface TickerRepository extends JpaRepository<Ticker, Long> {

    List<Ticker> findByActiveTrueOrderByPriorityDesc();

    // Fix all NULL active values to false
    @Modifying
    @Transactional
    @Query("UPDATE Ticker t SET t.active = false WHERE t.active IS NULL")
    void fixNullActiveValues();
}