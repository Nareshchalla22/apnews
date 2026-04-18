package com.news.apnews.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.news.apnews.model.Health;

@Repository
public interface HealthRepository extends JpaRepository<Health, Long> {}
