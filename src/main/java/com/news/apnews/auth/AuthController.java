package com.news.apnews.auth;
 
import com.news.apnews.model.AppUser;
import com.news.apnews.repository.AppUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
 
@RestController
@RequestMapping("/api/auth")
public class AuthController {
 
    @Autowired private AuthenticationManager authManager;
    @Autowired private JwtUtil jwtUtil;
    @Autowired private AppUserRepository userRepo;
    @Autowired private PasswordEncoder passwordEncoder;
 
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest req) {
        try {
            Authentication auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword())
            );
 
            AppUser user = userRepo.findByUsername(req.getUsername())
                .orElseThrow();
 
            String token = jwtUtil.generateToken(user.getUsername(), user.getRole());
 
            return ResponseEntity.ok(new AuthResponse(
                token,
                new AuthResponse.UserInfo(user.getId(), user.getUsername(), user.getRole())
            ));
 
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(401)
                .body(java.util.Map.of("message", "Invalid username or password"));
        }
    }
 
    // Register endpoint (disable in production or protect with ADMIN role)
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody AppUser newUser) {
        if (userRepo.findByUsername(newUser.getUsername()).isPresent()) {
            return ResponseEntity.badRequest()
                .body(java.util.Map.of("message", "Username already exists"));
        }
        newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));
        if (newUser.getRole() == null || newUser.getRole().isBlank()) {
            newUser.setRole("EDITOR");
        }
        AppUser saved = userRepo.save(newUser);
        return ResponseEntity.ok(java.util.Map.of(
            "id", saved.getId(),
            "username", saved.getUsername(),
            "role", saved.getRole()
        ));
    }
 
    @GetMapping("/me")
    public ResponseEntity<?> me(Authentication auth) {
        if (auth == null) return ResponseEntity.status(401).build();
        AppUser user = userRepo.findByUsername(auth.getName()).orElseThrow();
        return ResponseEntity.ok(new AuthResponse.UserInfo(
            user.getId(), user.getUsername(), user.getRole()
        ));
    }
}