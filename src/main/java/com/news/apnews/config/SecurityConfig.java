package com.news.apnews.config;

import com.news.apnews.auth.JwtAuthFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .cors(cors -> cors.configurationSource(corsSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth

                        // ── Public auth endpoints ──────────────────────────────────
                        .requestMatchers("/api/auth/**").permitAll()

                        // ── Public GET: news reading (no login required) ───────────
                        .requestMatchers(HttpMethod.GET, "/api/global").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/national").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/state").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/business").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/crime").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/entertainment").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/sports").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/health").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/politics").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/travel").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/technology").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/all").permitAll()
                        // Press pass GET is public (to display on site)
                        // POST/PUT/DELETE require login (handled by anyRequest below)
                        .requestMatchers(HttpMethod.GET, "/api/press-pass").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/press-pass/**").permitAll()

                        // ── Everything else requires authentication ─────────────────
                        .anyRequest().authenticated())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public CorsConfigurationSource corsSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of(
                "https://flash-news-ui.vercel.app",
                "http://localhost:5173"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
            throws Exception {
        return config.getAuthenticationManager();
    }
}