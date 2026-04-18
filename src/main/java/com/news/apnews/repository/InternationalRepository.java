package com.news.apnews.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.news.apnews.model.International;

@Repository
public interface InternationalRepository extends JpaRepository<International, Long> {}