package com.news.apnews.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.news.apnews.model.CategoryMenu;



@Repository
public interface CategoryMenuRepository extends JpaRepository<CategoryMenu, Long> {}

