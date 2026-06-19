package com.news.apnews.auth;

import com.news.apnews.model.AppUser;
import com.news.apnews.repository.AppUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private AppUserRepository userRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        AppUser appUser = userRepo.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "User not found: " + username));

        if (!appUser.isEnabled()) {
            throw new UsernameNotFoundException("User account is disabled: " + username);
        }

        // Normalise role — Spring Security expects ROLE_ prefix
        String role = appUser.getRole();
        String springRole = role.startsWith("ROLE_") ? role : "ROLE_" + role;

        return User.builder()
                .username(appUser.getUsername())
                .password(appUser.getPassword())
                .authorities(List.of(new SimpleGrantedAuthority(springRole)))
                .accountExpired(false)
                .accountLocked(!appUser.isEnabled())
                .credentialsExpired(false)
                .disabled(!appUser.isEnabled())
                .build();
    }
}