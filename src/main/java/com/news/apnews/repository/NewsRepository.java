package com.news.apnews.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.news.apnews.model.NewsItem;

public interface NewsRepository extends JpaRepository<NewsItem, Long> {

}
