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
import org.springframework.security.config.Customizer;


import java.util.List;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
            .cors(Customizer.withDefaults())
            .cors(cors -> cors.configurationSource(corsSource()))
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth

                // ── Allow ALL OPTIONS preflight requests ──────────────────────
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                // ── Public auth endpoints ─────────────────────────────────────
                .requestMatchers("/api/auth/login").permitAll()
                .requestMatchers("/api/auth/register").permitAll()
                .requestMatchers("/actuator/**").permitAll()

                // ── Public GET: news reading ──────────────────────────────────
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

                // ── Public: reporter application submit ───────────────────────
                .requestMatchers(HttpMethod.POST, "/api/reporter-application").permitAll()

                // ── REPORTER role: can POST (create) and PUT (update) news ────
                // Reporters can publish news but cannot delete or manage users
                .requestMatchers(HttpMethod.POST,
                    "/api/global", "/api/national", "/api/state",
                    "/api/business", "/api/crime", "/api/entertainment",
                    "/api/sports", "/api/health", "/api/politics",
                    "/api/travel", "/api/technology",
                    "/api/create"
                ).hasAnyRole("ADMIN", "REPORTER")

                .requestMatchers(HttpMethod.PUT,
                    "/api/global/**", "/api/national/**", "/api/state/**",
                    "/api/business/**", "/api/crime/**", "/api/entertainment/**",
                    "/api/sports/**", "/api/health/**", "/api/politics/**",
                    "/api/travel/**", "/api/technology/**",
                    "/api/update/**"
                ).hasAnyRole("ADMIN", "REPORTER")

                // ── DELETE and user management: ADMIN only ────────────────────
                .requestMatchers(HttpMethod.DELETE, "/**").hasRole("ADMIN")
                .requestMatchers("/api/auth/users/**").hasRole("ADMIN")
                .requestMatchers("/api/auth/activate-reporter/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/reporter-application/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/reporter-application/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/reporter-application/**").hasRole("ADMIN")

                // ── Everything else requires login ────────────────────────────
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
            .build();
    }

    @Bean
    public CorsConfigurationSource corsSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOriginPatterns(List.of(
            "https://main.d1sgj1iof00zuq.amplifyapp.com",
            "https://*.amplifyapp.com",
            "http://localhost:*",
            "http://127.0.0.1:*",
            "https://ap13news.in",
            "https://www.ap13news.in"
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
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
            throws Exception {
        return config.getAuthenticationManager();
    }
}