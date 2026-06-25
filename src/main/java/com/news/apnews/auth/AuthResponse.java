package com.news.apnews.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {
    private String   token;
    private UserInfo user;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UserInfo {
        private Long   id;
        private String username;
        private String role;
        private String fullName;
        private String email;
        private String phone;
        private String planId;
        private String planName;
        private String photoUrl;
        private boolean enabled;
    }
}