package com.news.apnews.auth;

import com.news.apnews.model.AppUser;
import com.news.apnews.model.ReporterApplication;
import com.news.apnews.repository.AppUserRepository;
import com.news.apnews.repository.ReporterApplicationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired private AuthenticationManager authManager;
    @Autowired private JwtUtil jwtUtil;
    @Autowired private AppUserRepository userRepo;
    @Autowired private ReporterApplicationRepository reporterRepo;
    @Autowired private PasswordEncoder passwordEncoder;

    // ── LOGIN ──────────────────────────────────────────────────────────────────
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest req) {
        try {
            Authentication auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword())
            );

            AppUser user = userRepo.findByUsername(req.getUsername()).orElseThrow();
            String token = jwtUtil.generateToken(user.getUsername(), user.getRole());

            return ResponseEntity.ok(new AuthResponse(
                token,
                new AuthResponse.UserInfo(
                    user.getId(),
                    user.getUsername(),
                    user.getRole(),
                    user.getFullName(),
                    user.getPlanName()
                )
            ));

        } catch (BadCredentialsException e) {
            return ResponseEntity.status(401)
                .body(Map.of("message", "Invalid username or password"));
        }
    }

    // ── REGISTER (admin use only) ───────────────────────────────────────────────
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody AppUser newUser) {
        if (userRepo.findByUsername(newUser.getUsername()).isPresent()) {
            return ResponseEntity.badRequest()
                .body(Map.of("message", "Username already exists"));
        }
        newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));
        if (newUser.getRole() == null || newUser.getRole().isBlank()) {
            newUser.setRole("REPORTER");
        }
        AppUser saved = userRepo.save(newUser);
        return ResponseEntity.ok(Map.of(
            "id",       saved.getId(),
            "username", saved.getUsername(),
            "role",     saved.getRole()
        ));
    }

    // ── ACTIVATE REPORTER ───────────────────────────────────────────────────────
    // Called when admin approves a reporter application — creates login credentials
    @PostMapping("/activate-reporter/{applicationId}")
    public ResponseEntity<?> activateReporter(
            @PathVariable Long applicationId,
            @RequestBody Map<String, String> body) {

        ReporterApplication app = reporterRepo.findById(applicationId)
            .orElse(null);
        if (app == null) {
            return ResponseEntity.notFound().build();
        }
        if (!"APPROVED".equals(app.getStatus())) {
            return ResponseEntity.badRequest()
                .body(Map.of("message", "Application must be APPROVED before activating"));
        }

        String username = body.getOrDefault("username",
            app.getFullName().toLowerCase().replaceAll("\\s+", "."));
        String password = body.getOrDefault("password",
            "AP13@" + app.getTxnId().substring(app.getTxnId().length() - 6));

        // Check if user already exists
        if (userRepo.findByUsername(username).isPresent()) {
            return ResponseEntity.badRequest()
                .body(Map.of("message", "Username '" + username + "' already taken. Provide a different one."));
        }

        AppUser reporter = new AppUser();
        reporter.setUsername(username);
        reporter.setPassword(passwordEncoder.encode(password));
        reporter.setRole("REPORTER");
        reporter.setEnabled(true);
        reporter.setFullName(app.getFullName());
        reporter.setEmail(app.getEmail());
        reporter.setPhone(app.getPhone());
        reporter.setPlanId(app.getPlanId());
        reporter.setPlanName(app.getPlanName());

        userRepo.save(reporter);

        return ResponseEntity.ok(Map.of(
            "message",  "Reporter account created successfully",
            "username", username,
            "password", password,   // shown once — admin shares this with reporter
            "role",     "REPORTER",
            "planName", app.getPlanName() != null ? app.getPlanName() : ""
        ));
    }

    // ── LIST ALL USERS ──────────────────────────────────────────────────────────
    @GetMapping("/users")
    public ResponseEntity<?> listUsers() {
        List<AppUser> users = userRepo.findAll();
        return ResponseEntity.ok(
            users.stream().map(u -> Map.of(
                "id",       u.getId(),
                "username", u.getUsername(),
                "role",     u.getRole(),
                "fullName", u.getFullName() != null ? u.getFullName() : "",
                "planName", u.getPlanName() != null ? u.getPlanName() : "",
                "enabled",  u.isEnabled()
            )).toList()
        );
    }

    // ── TOGGLE USER ENABLED ─────────────────────────────────────────────────────
    @PatchMapping("/users/{id}/toggle")
    public ResponseEntity<?> toggleUser(@PathVariable Long id) {
        return userRepo.findById(id).map(u -> {
            u.setEnabled(!u.isEnabled());
            userRepo.save(u);
            return ResponseEntity.ok(Map.of(
                "id",      u.getId(),
                "enabled", u.isEnabled(),
                "message", u.isEnabled() ? "User enabled" : "User disabled"
            ));
        }).orElse(ResponseEntity.notFound().build());
    }

    // ── ME ──────────────────────────────────────────────────────────────────────
    @GetMapping("/me")
    public ResponseEntity<?> me(Authentication auth) {
        if (auth == null) return ResponseEntity.status(401).build();
        AppUser user = userRepo.findByUsername(auth.getName()).orElseThrow();
        return ResponseEntity.ok(new AuthResponse.UserInfo(
            user.getId(), user.getUsername(), user.getRole(),
            user.getFullName(), user.getPlanName()
        ));
    }
}