package com.news.apnews.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// ERROR WAS HERE: Both @Configuration AND @Component were on this class.
// @Configuration already implies @Component — having both causes
// Spring to try registering the beans TWICE → BeanDefinitionOverrideException
// FIX: Remove @Component, keep only @Configuration
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

@Configuration
// REMOVED: @Component  ← this was the duplicate causing the conflict
public class AwsConfig {

    @Value("${aws.access.key:}")
    private String accessKey;

    @Value("${aws.secret.key:}")
    private String secretKey;

    @Value("${aws.s3.region:us-east-1}")
    private String region;

    @Bean
    public S3Client s3Client() {
        // If keys are blank, fall back to EC2 IAM role credentials
        if (accessKey == null || accessKey.isBlank()) {
            return S3Client.builder()
                .region(Region.of(region))
                .build();
        }
        AwsBasicCredentials credentials =
            AwsBasicCredentials.create(accessKey, secretKey);
        return S3Client.builder()
            .region(Region.of(region))
            .credentialsProvider(StaticCredentialsProvider.create(credentials))
            .build();
    }

    @Bean
    public S3Presigner s3Presigner() {
        if (accessKey == null || accessKey.isBlank()) {
            return S3Presigner.builder()
                .region(Region.of(region))
                .build();
        }
        AwsBasicCredentials credentials =
            AwsBasicCredentials.create(accessKey, secretKey);
        return S3Presigner.builder()
            .region(Region.of(region))
            .credentialsProvider(StaticCredentialsProvider.create(credentials))
            .build();
    }
}