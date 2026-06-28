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

                // ── Allow ALL OPTIONS preflight ───────────────────────────────
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                // ── Public auth ───────────────────────────────────────────────
                .requestMatchers("/api/auth/login").permitAll()
                .requestMatchers("/api/auth/register").permitAll()
                .requestMatchers("/actuator/**").permitAll()

                // ── Public GET news ───────────────────────────────────────────
                .requestMatchers(HttpMethod.GET, "/api/global/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/national/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/state/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/business/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/crime/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/entertainment/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/sports/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/health/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/politics/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/travel/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/technology/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/international/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/all").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/all/**").permitAll()

                // ── Public ads ────────────────────────────────────────────────
                .requestMatchers(HttpMethod.GET, "/api/ads/active").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/ads/placement/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/ads/type/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/ads/*/impression").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/ads/*/click").permitAll()

                // ── Public reporter apply ─────────────────────────────────────
                .requestMatchers(HttpMethod.POST, "/api/reporter-application").permitAll()

                // ── REPORTER + ADMIN: publish news ────────────────────────────
                // Using hasAnyAuthority with ROLE_ prefix (explicit, no ambiguity)
                .requestMatchers(HttpMethod.POST,
                    "/api/global", "/api/national", "/api/state",
                    "/api/business", "/api/crime", "/api/entertainment",
                    "/api/sports", "/api/health", "/api/politics",
                    "/api/travel", "/api/technology", "/api/create"
                ).hasAnyAuthority("ROLE_ADMIN", "ROLE_REPORTER")

                .requestMatchers(HttpMethod.PUT,
                    "/api/global/**", "/api/national/**", "/api/state/**",
                    "/api/business/**", "/api/crime/**", "/api/entertainment/**",
                    "/api/sports/**", "/api/health/**", "/api/politics/**",
                    "/api/travel/**", "/api/technology/**", "/api/update/**"
                ).hasAnyAuthority("ROLE_ADMIN", "ROLE_REPORTER")

                // ── ADMIN only ────────────────────────────────────────────────
                // Using hasAuthority("ROLE_ADMIN") — explicit, no hasRole() prefix confusion
                .requestMatchers(HttpMethod.DELETE, "/**")
                    .hasAuthority("ROLE_ADMIN")

                .requestMatchers("/api/auth/users/**")
                    .hasAuthority("ROLE_ADMIN")

                .requestMatchers("/api/auth/activate-reporter/**")
                    .hasAuthority("ROLE_ADMIN")

                .requestMatchers(HttpMethod.GET,  "/api/reporter-application/**")
                    .hasAuthority("ROLE_ADMIN")
                .requestMatchers(HttpMethod.PUT,  "/api/reporter-application/**")
                    .hasAuthority("ROLE_ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/reporter-application/**")
                    .hasAuthority("ROLE_ADMIN")

                .requestMatchers("/api/ticker/**")
                    .hasAuthority("ROLE_ADMIN")
                .requestMatchers("/api/create")
                    .hasAnyAuthority("ROLE_ADMIN", "ROLE_REPORTER")
                .requestMatchers("/api/update/**")
                    .hasAnyAuthority("ROLE_ADMIN", "ROLE_REPORTER")

                // ── Ads FULL CRUD — ADMIN only ────────────────────────────────
                // GET /api/ads/active etc. already permitted above
                // Everything else under /api/ads/** requires ADMIN
                .requestMatchers(HttpMethod.GET,    "/api/ads/**").hasAuthority("ROLE_ADMIN")
                .requestMatchers(HttpMethod.POST,   "/api/ads/**").hasAuthority("ROLE_ADMIN")
                .requestMatchers(HttpMethod.PUT,    "/api/ads/**").hasAuthority("ROLE_ADMIN")
                .requestMatchers(HttpMethod.PATCH,  "/api/ads/**").hasAuthority("ROLE_ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/ads/**").hasAuthority("ROLE_ADMIN")

                // ── Everything else needs auth ────────────────────────────────
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
            .build();
    }

    @Bean
    public CorsConfigurationSource corsSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOriginPatterns(List.of(
            "https://ap13news.in",
            "https://www.ap13news.in",
            "https://*.amplifyapp.com",
            "http://localhost:*",
            "http://127.0.0.1:*"
        ));
        config.setAllowedMethods(List.of(
            "GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH", "HEAD"
        ));
        config.setAllowedHeaders(List.of("*"));
        config.setExposedHeaders(List.of("Authorization", "Content-Type"));
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
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}