package com.news.apnews.repository;

import com.news.apnews.model.National;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NationalRepository extends JpaRepository<National, Long> {}