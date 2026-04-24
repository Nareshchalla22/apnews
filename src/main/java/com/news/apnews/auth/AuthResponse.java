package com.news.apnews.auth;
 
import lombok.AllArgsConstructor;
import lombok.Data;
 
@Data
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private UserInfo user;
 
    @Data
    @AllArgsConstructor
    public static class UserInfo {
        private Long id;
        private String username;
        private String role;
    }
}