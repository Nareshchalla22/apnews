package com.news.apnews;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.news.apnews.model.AppUser;
import com.news.apnews.repository.AppUserRepository;

@SpringBootApplication
@EntityScan("com.news.apnews.model")
@EnableJpaRepositories("com.news.apnews.repository")
public class ApnewsApplication {
    public static void main(String[] args) {
        SpringApplication.run(ApnewsApplication.class, args);
    }

    @Bean
    CommandLineRunner seedAdmin(AppUserRepository repo, PasswordEncoder encoder) {
        return args -> {
            if (repo.findByUsername("admin").isEmpty()) {
                AppUser admin = new AppUser();
                admin.setUsername("admin");
                admin.setPassword(encoder.encode("AP13Admin@2026"));
                admin.setRole("ADMIN");
                admin.setEnabled(true);
                repo.save(admin);
                System.out.println("✅ Admin user seeded: admin / AP13Admin@2026");
            }
        };
    }
}