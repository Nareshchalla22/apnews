package com.news.apnews.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadBucketRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.io.IOException;
import java.time.Duration;
import java.util.UUID;

@Service
public class S3service {

    private static final Logger log = LoggerFactory.getLogger(S3service.class);

    @Autowired
    private S3Client s3Client;

    @Autowired
    private S3Presigner s3Presigner;

    @Value("${aws.s3.bucket}")
    private String bucket;

    @Value("${aws.s3.base-url}")
    private String baseUrl;

    // ── Upload MultipartFile (from REST endpoint) ────────────────────────────
    public String uploadFile(MultipartFile file, String folder) throws IOException {
        String ext       = getExtension(file.getOriginalFilename());
        String key       = folder + "/" + UUID.randomUUID() + ext;
        String contentType = file.getContentType() != null
            ? file.getContentType() : "application/octet-stream";

        PutObjectRequest req = PutObjectRequest.builder()
            .bucket(bucket)
            .key(key)
            .contentType(contentType)
            .contentLength(file.getSize())
            .build();

        s3Client.putObject(req, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

        String url = baseUrl + "/" + key;
        log.info("S3 upload success: {}", url);
        return url;
    }

    // ── Upload raw bytes (for Base64 images from frontend) ───────────────────
    public String uploadBytes(byte[] data, String folder, String ext, String contentType) {
        String key = folder + "/" + UUID.randomUUID() + ext;

        PutObjectRequest req = PutObjectRequest.builder()
            .bucket(bucket)
            .key(key)
            .contentType(contentType)
            .contentLength((long) data.length)
            .build();

        s3Client.putObject(req, RequestBody.fromBytes(data));

        String url = baseUrl + "/" + key;
        log.info("S3 bytes upload: {}", url);
        return url;
    }

    // ── Upload Base64 string (strips the data:image/... prefix) ─────────────
    public String uploadBase64(String base64, String folder) {
        if (base64 == null || base64.isBlank()) return null;

        // e.g. "data:image/jpeg;base64,/9j/4AAQ..."
        String[] parts = base64.split(",", 2);
        if (parts.length != 2) return null;

        String meta        = parts[0];           // "data:image/jpeg;base64"
        String data        = parts[1];
        byte[] bytes       = java.util.Base64.getDecoder().decode(data);
        String contentType = meta.replace("data:", "").replace(";base64", "");
        String ext         = "." + contentType.split("/")[1].replace("jpeg", "jpg");

        return uploadBytes(bytes, folder, ext, contentType);
    }

    // ── Delete a file by full URL ────────────────────────────────────────────
    public void deleteByUrl(String fileUrl) {
        if (fileUrl == null || !fileUrl.contains(bucket)) return;
        try {
            // Extract key from URL: https://bucket.s3.region.amazonaws.com/folder/file.jpg
            String key = fileUrl.substring(fileUrl.indexOf(bucket) + bucket.length() + 1);
            // Remove leading slash if baseUrl format differs
            if (key.startsWith("/")) key = key.substring(1);

            s3Client.deleteObject(DeleteObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build());
            log.info("S3 delete: {}", key);
        } catch (Exception e) {
            log.warn("S3 delete failed for {}: {}", fileUrl, e.getMessage());
        }
    }

    // ── Generate presigned URL (time-limited private access) ────────────────
    public String presignedUrl(String key, int expiryMinutes) {
        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
            .signatureDuration(Duration.ofMinutes(expiryMinutes))
            .getObjectRequest(r -> r.bucket(bucket).key(key))
            .build();

        return s3Presigner.presignGetObject(presignRequest)
            .url().toString();
    }

    // ── Check bucket connectivity (health endpoint) ───────────────────────────
    public boolean isConnected() {
        try {
            s3Client.headBucket(HeadBucketRequest.builder().bucket(bucket).build());
            return true;
        } catch (Exception e) {
            log.warn("S3 health check failed: {}", e.getMessage());
            return false;
        }
    }

    private String getExtension(String filename) {
        if (filename == null || !filename.contains(".")) return ".jpg";
        return filename.substring(filename.lastIndexOf(".")).toLowerCase();
    }
}