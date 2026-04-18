package com.news.apnews;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan("com.news.apnews.model")
@EnableJpaRepositories("com.news.apnews.repository")
public class ApnewsApplication {
    public static void main(String[] args) {
        SpringApplication.run(ApnewsApplication.class, args);
    }
}