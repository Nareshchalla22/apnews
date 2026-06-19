package com.news.apnews.repository;

import com.news.apnews.model.Advertisement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AdvertisementRepository extends JpaRepository<Advertisement, Long> {

    // All active ads sorted by priority
    List<Advertisement> findByActiveTrueOrderByPriorityAsc();

    // Active ads by placement
    List<Advertisement> findByActiveTrueAndPlacementOrderByPriorityAsc(String placement);

    // Active ads by type
    List<Advertisement> findByActiveTrueAndTypeOrderByPriorityAsc(String type);

    // Active ads by placement OR "all"
    @Query("SELECT a FROM Advertisement a WHERE a.active = true AND (a.placement = :placement OR a.placement = 'all') ORDER BY a.priority ASC")
    List<Advertisement> findActiveByPlacement(String placement);

    // Active and within schedule
    @Query("SELECT a FROM Advertisement a WHERE a.active = true AND (a.startDate IS NULL OR a.startDate <= :now) AND (a.endDate IS NULL OR a.endDate >= :now) ORDER BY a.priority ASC")
    List<Advertisement> findActiveAndScheduled(LocalDateTime now);

    // Increment impressions
    @Modifying
    @Transactional
    @Query("UPDATE Advertisement a SET a.impressions = a.impressions + 1 WHERE a.id = :id")
    void incrementImpressions(Long id);

    // Increment clicks
    @Modifying
    @Transactional
    @Query("UPDATE Advertisement a SET a.clicks = a.clicks + 1 WHERE a.id = :id")
    void incrementClicks(Long id);

    // All by type
    List<Advertisement> findByTypeOrderByPriorityAsc(String type);
}