package com.news.apnews.repository;

import com.news.apnews.model.Crime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CrimeRepository extends JpaRepository<Crime, Long> {}