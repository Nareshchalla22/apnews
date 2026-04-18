package com.news.apnews.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.news.apnews.model.Politics;

@Repository
public interface PoliticsRepository extends JpaRepository<Politics, Long> {}