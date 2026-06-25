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

    @Autowired private AuthenticationManager         authManager;
    @Autowired private JwtUtil                       jwtUtil;
    @Autowired private AppUserRepository             userRepo;
    @Autowired private ReporterApplicationRepository reporterRepo;
    @Autowired private PasswordEncoder               passwordEncoder;

    // ── Helper: build full UserInfo from AppUser ──────────────────────────────
    private Map<String, Object> buildUserInfo(AppUser u) {
        return Map.ofEntries(
            Map.entry("id",       u.getId()),
            Map.entry("username", u.getUsername()),
            Map.entry("role",     u.getRole()),
            Map.entry("fullName", u.getFullName()  != null ? u.getFullName()  : ""),
            Map.entry("email",    u.getEmail()     != null ? u.getEmail()     : ""),
            Map.entry("phone",    u.getPhone()     != null ? u.getPhone()     : ""),
            Map.entry("planId",   u.getPlanId()    != null ? u.getPlanId()    : ""),
            Map.entry("planName", u.getPlanName()  != null ? u.getPlanName()  : ""),
            Map.entry("photoUrl", u.getPhotoUrl()  != null ? u.getPhotoUrl()  : ""),
            Map.entry("enabled",  u.isEnabled())
        );
    }

    // ── LOGIN ─────────────────────────────────────────────────────────────────
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest req) {
        try {
            authManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    req.getUsername(), req.getPassword())
            );

            AppUser user  = userRepo.findByUsername(req.getUsername()).orElseThrow();
            String  token = jwtUtil.generateToken(user.getUsername(), user.getRole());

            // Return token + FULL user profile so frontend can show
            // reporter name, Press ID card, plan info, photo etc.
            return ResponseEntity.ok(Map.of(
                "token", token,
                "user",  buildUserInfo(user)
            ));

        } catch (BadCredentialsException e) {
            return ResponseEntity.status(401)
                .body(Map.of("message", "Invalid username or password"));
        } catch (DisabledException e) {
            return ResponseEntity.status(403)
                .body(Map.of("message", "Account is disabled. Contact admin."));
        } catch (LockedException e) {
            return ResponseEntity.status(403)
                .body(Map.of("message", "Account is locked. Contact admin."));
        }
    }

    // ── REGISTER (admin use only) ─────────────────────────────────────────────
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
        return ResponseEntity.ok(buildUserInfo(saved));
    }

    // ── ACTIVATE REPORTER ─────────────────────────────────────────────────────
    @PostMapping("/activate-reporter/{applicationId}")
    public ResponseEntity<?> activateReporter(
            @PathVariable Long applicationId,
            @RequestBody   Map<String, String> body) {

        ReporterApplication app = reporterRepo.findById(applicationId).orElse(null);
        if (app == null) return ResponseEntity.notFound().build();

        if (!"APPROVED".equals(app.getStatus())) {
            return ResponseEntity.badRequest()
                .body(Map.of("message",
                    "Application must be APPROVED before activating"));
        }

        // Auto-generate credentials if not provided
        String username = body.getOrDefault("username",
            app.getFullName().toLowerCase().replaceAll("\\s+", "."));
        String password = body.getOrDefault("password",
            "AP13@" + app.getTxnId()
                .substring(Math.max(0, app.getTxnId().length() - 6)));

        if (userRepo.findByUsername(username).isPresent()) {
            return ResponseEntity.badRequest()
                .body(Map.of("message",
                    "Username '" + username + "' already taken."));
        }

        // Build reporter account — copy ALL profile data from application
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
        reporter.setPhotoUrl(app.getPhotoUrl()); // for Press ID card photo

        userRepo.save(reporter);

        return ResponseEntity.ok(Map.of(
            "message",  "Reporter account created successfully",
            "username", username,
            "password", password,  // shown once — admin shares with reporter
            "role",     "REPORTER",
            "fullName", app.getFullName()  != null ? app.getFullName()  : "",
            "planName", app.getPlanName()  != null ? app.getPlanName()  : "",
            "planId",   app.getPlanId()    != null ? app.getPlanId()    : ""
        ));
    }

    // ── LIST ALL USERS ────────────────────────────────────────────────────────
    @GetMapping("/users")
    public ResponseEntity<?> listUsers() {
        return ResponseEntity.ok(
            userRepo.findAll().stream()
                .map(this::buildUserInfo)
                .toList()
        );
    }

    // ── TOGGLE USER ENABLED ───────────────────────────────────────────────────
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

    // ── ME — returns current logged-in user's full profile ────────────────────
    @GetMapping("/me")
    public ResponseEntity<?> me(Authentication auth) {
        if (auth == null) return ResponseEntity.status(401).build();
        AppUser user = userRepo.findByUsername(auth.getName()).orElseThrow();
        return ResponseEntity.ok(buildUserInfo(user));
    }
}