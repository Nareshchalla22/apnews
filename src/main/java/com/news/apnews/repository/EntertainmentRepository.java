package com.news.apnews.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.news.apnews.model.Entertainment;

@Repository
public interface EntertainmentRepository extends JpaRepository<Entertainment, Long> {}