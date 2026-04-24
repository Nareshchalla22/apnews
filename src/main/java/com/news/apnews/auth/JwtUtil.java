package com.news.apnews.auth;
 
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
 
import javax.crypto.SecretKey;
import java.util.Date;
 
@Component
public class JwtUtil {
 
    @Value("${app.jwt.secret}")
    private String secret;
 
    @Value("${app.jwt.expiration:86400000}")
    private long expiration;
 
    private SecretKey getKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }
 
    public String generateToken(String username, String role) {
        return Jwts.builder()
            .subject(username)
            .claim("role", role)
            .issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis() + expiration))
            .signWith(getKey())
            .compact();
    }
 
    public String extractUsername(String token) {
        return Jwts.parser().verifyWith(getKey()).build()
            .parseSignedClaims(token).getPayload().getSubject();
    }
 
    public boolean isValid(String token, UserDetails userDetails) {
        try {
            String username = extractUsername(token);
            return username.equals(userDetails.getUsername())
                && !isExpired(token);
        } catch (JwtException e) {
            return false;
        }
    }
 
    private boolean isExpired(String token) {
        return Jwts.parser().verifyWith(getKey()).build()
            .parseSignedClaims(token).getPayload()
            .getExpiration().before(new Date());
    }
}