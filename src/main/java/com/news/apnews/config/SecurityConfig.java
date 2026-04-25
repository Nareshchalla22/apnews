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

                // ── FIX #1: Allow ALL OPTIONS preflight requests ──────────
                // Browser sends OPTIONS before every POST/PUT/DELETE.
                // Without this, CORS preflight fails → login never fires.
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                // ── Public auth endpoints ─────────────────────────────────
                .requestMatchers("/api/auth/**").permitAll()

                // ── Public GET: news reading ──────────────────────────────
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
                .requestMatchers(HttpMethod.GET, "/api/press-pass").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/press-pass/**").permitAll()

                // ── Reporter registration is public ───────────────────────
                .requestMatchers(HttpMethod.POST, "/api/reporter-application").permitAll()

                // ── Everything else requires login ────────────────────────
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
            .build();
    }

    @Bean
    public CorsConfigurationSource corsSource() {
        CorsConfiguration config = new CorsConfiguration();

        // ── FIX #2: Use allowedOriginPatterns instead of allowedOrigins ───
        // allowedOriginPatterns supports wildcards and works with
        // allowCredentials(true) — allowedOrigins("*") does NOT.
        // This covers your production URL + all Vercel preview deployments.
        config.setAllowedOriginPatterns(List.of(
            "https://flash-news-ui.vercel.app",   // production
            "https://*.vercel.app",               // Vercel preview URLs
            "http://localhost:*",                 // local dev any port
            "http://127.0.0.1:*"                  // local dev alternate
        ));

        config.setAllowedMethods(List.of(
            "GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH", "HEAD"
        ));

        // Allow all headers including Authorization
        config.setAllowedHeaders(List.of("*"));

        // Expose Authorization header so frontend can read it
        config.setExposedHeaders(List.of("Authorization", "Content-Type"));

        config.setAllowCredentials(true);

        // Cache preflight for 1 hour — reduces OPTIONS round trips
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