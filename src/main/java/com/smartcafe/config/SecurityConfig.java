package com.smartcafe.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * SecurityConfig - Spring Security Configuration
 * 
 * For this demo application, security is configured permissively
 * to allow easy testing. In production, you would want to:
 * - Require authentication for admin/kitchen endpoints
 * - Protect API endpoints with JWT or session-based auth
 * - Enable CSRF protection
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Configure HTTP security
     * 
     * Current setup: All endpoints are publicly accessible
     * This is for demonstration purposes only!
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // Disable CSRF for REST API (enable for production with proper handling)
                .csrf(csrf -> csrf.disable())

                // Configure authorization rules
                .authorizeHttpRequests(auth -> auth
                        // Allow all requests (permissive for demo)
                        .anyRequest().permitAll())

                // Disable frame options for H2 Console (if used)
                .headers(headers -> headers
                        .frameOptions(frame -> frame.sameOrigin()));

        return http.build();
    }

    /**
     * Password encoder bean for hashing passwords
     * BCrypt is a secure hashing algorithm with salt
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
