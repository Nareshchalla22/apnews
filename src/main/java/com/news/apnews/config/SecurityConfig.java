package com.news.apnews.config;

import com.news.apnews.auth.JwtAuthFilter;
import com.news.apnews.auth.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
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
@EnableWebSecurity
public class SecurityConfig {

    @Autowired private JwtAuthFilter jwtAuthFilter;
    @Autowired private UserDetailsServiceImpl userDetailsService;

    // ── Filter chain ───────────────────────────────────────────────────────
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth

                // ── Public auth ──────────────────────────────────────────────
                .requestMatchers(
                    "/api/auth/login",
                    "/api/auth/register"
                ).permitAll()

                // ── Public reporter application ──────────────────────────────
                .requestMatchers(HttpMethod.POST, "/api/reporter-application").permitAll()

                // ── Public ticker ────────────────────────────────────────────
                .requestMatchers(HttpMethod.GET, "/api/all", "/api/all/active").permitAll()

                // ── Public news (GET only) ───────────────────────────────────
                .requestMatchers(HttpMethod.GET,
                    "/api/global/**",
                    "/api/national/**",
                    "/api/state/**",
                    "/api/business/**",
                    "/api/crime/**",
                    "/api/entertainment/**",
                    "/api/sports/**",
                    "/api/health/**",
                    "/api/politics/**",
                    "/api/travel/**",
                    "/api/trending/**"
                ).permitAll()

                // ── Public meta tags (WhatsApp/Telegram bots) ────────────────
                .requestMatchers("/category/**").permitAll()

                // ── Public ads — active ads for homepage display ─────────────
                .requestMatchers(HttpMethod.GET,
                    "/api/ads/active",
                    "/api/ads/placement/**",
                    "/api/ads/type/**"
                ).permitAll()

                // ── Public ad tracking (click/impression) ─────────────────────
                .requestMatchers(HttpMethod.POST,
                    "/api/ads/*/impression",
                    "/api/ads/*/click"
                ).permitAll()

                // ── Reporter: publish news ────────────────────────────────────
                .requestMatchers(HttpMethod.POST,
                    "/api/global/**", "/api/national/**", "/api/state/**",
                    "/api/business/**", "/api/crime/**", "/api/entertainment/**",
                    "/api/sports/**", "/api/health/**", "/api/politics/**"
                ).hasAnyRole("REPORTER", "ADMIN")

                .requestMatchers(HttpMethod.PUT,
                    "/api/global/**", "/api/national/**", "/api/state/**",
                    "/api/business/**", "/api/crime/**", "/api/entertainment/**",
                    "/api/sports/**", "/api/health/**", "/api/politics/**"
                ).hasAnyRole("REPORTER", "ADMIN")

                // ── Admin only ────────────────────────────────────────────────
                .requestMatchers(HttpMethod.DELETE, "/api/**").hasRole("ADMIN")
                .requestMatchers("/api/auth/users/**").hasRole("ADMIN")
                .requestMatchers("/api/auth/activate-reporter/**").hasRole("ADMIN")
                .requestMatchers("/api/reporter-application/**").hasRole("ADMIN")
                .requestMatchers("/api/create", "/api/update/**", "/api/ticker/**").hasRole("ADMIN")

                // ── Ads CRUD — admin only ─────────────────────────────────────
                .requestMatchers("/api/ads/**").hasRole("ADMIN")

                // ── Everything else requires authentication ────────────────────
                .anyRequest().authenticated()
            )
            .authenticationProvider(authenticationProvider())
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // ── Auth provider — wires UserDetailsService + PasswordEncoder ─────────
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    // ── CORS ───────────────────────────────────────────────────────────────
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOriginPatterns(List.of(
            "https://ap13news.in",
            "https://www.ap13news.in",
            "https://*.amplifyapp.com",
            "http://localhost:*"
        ));
        config.setAllowedMethods(List.of("GET","POST","PUT","DELETE","PATCH","OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    // ── Beans ──────────────────────────────────────────────────────────────
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}