package com.news.apnews.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {
    private String token;
    private UserInfo user;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor   // ← Critical: Jackson needs this to deserialize inner class
    public static class UserInfo {
        private Long id;
        private String username;
        private String role;
    }
}