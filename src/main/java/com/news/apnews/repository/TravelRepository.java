package com.news.apnews.repository;

import com.news.apnews.model.Travel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TravelRepository extends JpaRepository<Travel, Long> {}