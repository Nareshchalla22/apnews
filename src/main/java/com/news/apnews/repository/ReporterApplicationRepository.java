package com.news.apnews.repository;

import com.news.apnews.model.ReporterApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReporterApplicationRepository extends JpaRepository<ReporterApplication, Long> {
    List<ReporterApplication> findByStatus(String status);
    List<ReporterApplication> findByEmailIgnoreCase(String email);
    boolean existsByTxnId(String txnId);
}