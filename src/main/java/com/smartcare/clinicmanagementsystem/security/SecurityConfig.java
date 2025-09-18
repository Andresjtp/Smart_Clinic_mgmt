package com.smartcare.clinicmanagementsystem.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Security Configuration
 * Configures Spring Security with JWT authentication
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private UserDetailsService userDetailsService;

    /**
     * Configure Security Filter Chain
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(authz -> authz
                // Public endpoints
                .requestMatchers("/", "/login", "/register", "/forgot-password").permitAll()
                .requestMatchers("/css/**", "/js/**", "/images/**", "/favicon.ico").permitAll()
                .requestMatchers("/error", "/actuator/health").permitAll()
                
                // API endpoints
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/public/**").permitAll()
                
                // Role-based endpoints
                .requestMatchers("/admin/**", "/api/admin/**").hasRole("ADMIN")
                .requestMatchers("/doctor/**", "/api/doctor/**").hasAnyRole("ADMIN", "DOCTOR")
                .requestMatchers("/patient/**", "/api/patient/**").hasAnyRole("ADMIN", "DOCTOR", "PATIENT")
                
                // All other requests require authentication
                .anyRequest().authenticated()
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .authenticationProvider(authenticationProvider())
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            
            // Custom error handling
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint((request, response, authException) -> {
                    String requestURI = request.getRequestURI();
                    
                    // For API endpoints, return JSON error
                    if (requestURI.startsWith("/api/")) {
                        response.setStatus(401);
                        response.setContentType("application/json");
                        response.getWriter().write("{\"success\":false,\"message\":\"Unauthorized access\"}");
                    } else {
                        // For web endpoints, redirect to login
                        response.sendRedirect("/login");
                    }
                })
                .accessDeniedHandler((request, response, accessDeniedException) -> {
                    String requestURI = request.getRequestURI();
                    
                    // For API endpoints, return JSON error
                    if (requestURI.startsWith("/api/")) {
                        response.setStatus(403);
                        response.setContentType("application/json");
                        response.getWriter().write("{\"success\":false,\"message\":\"Access denied\"}");
                    } else {
                        // For web endpoints, redirect to error page
                        response.sendRedirect("/error?code=403");
                    }
                })
            );

        return http.build();
    }

    /**
     * Authentication Provider
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    /**
     * Authentication Manager
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * Password Encoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}