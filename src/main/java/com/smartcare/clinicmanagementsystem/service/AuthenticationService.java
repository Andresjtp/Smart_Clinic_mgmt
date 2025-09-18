package com.smartcare.clinicmanagementsystem.service;

import com.smartcare.clinicmanagementsystem.model.User;
import com.smartcare.clinicmanagementsystem.security.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Authentication Service
 * Handles user authentication, JWT token generation and validation
 */
@Service
public class AuthenticationService {

    @Autowired
    private UserService userService;
    
    @Autowired
    private JwtService jwtService;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    /**
     * Authenticate user with email and password
     */
    public User authenticate(String email, String password) {
        User user = userService.findByEmail(email);
        if (user != null && user.getActive()) {
            // In production, use proper password hashing (BCrypt)
            // For demo purposes, we're doing plain text comparison
            if (password.equals(user.getPassword())) {
                return user;
            }
            // For production with encrypted passwords:
            // if (passwordEncoder.matches(password, user.getPassword())) {
            //     return user;
            // }
        }
        return null;
    }
    
    /**
     * Generate JWT token for authenticated user
     */
    public String generateToken(User user) {
        return jwtService.generateToken(user.getEmail(), user.getRole().name(), user.getId());
    }
    
    /**
     * Generate refresh token
     */
    public String generateRefreshToken(User user) {
        return jwtService.generateRefreshToken(user.getEmail());
    }
    
    /**
     * Validate JWT token and return user
     */
    public User validateToken(String token) {
        if (jwtService.isTokenValid(token)) {
            String email = jwtService.extractUsername(token);
            return userService.findByEmail(email);
        }
        return null;
    }
    
    /**
     * Refresh JWT token
     */
    public String refreshToken(String refreshToken) {
        if (jwtService.isTokenValid(refreshToken) && jwtService.isRefreshToken(refreshToken)) {
            String email = jwtService.extractUsername(refreshToken);
            User user = userService.findByEmail(email);
            if (user != null && user.getActive()) {
                return generateToken(user);
            }
        }
        return null;
    }
    
    /**
     * Check if token is valid
     */
    public boolean isTokenValid(String token) {
        return jwtService.isTokenValid(token);
    }
    
    /**
     * Get user from token
     */
    public User getUserFromToken(String token) {
        return validateToken(token);
    }
    
    /**
     * Hash password (for registration)
     */
    public String hashPassword(String plainPassword) {
        return passwordEncoder.encode(plainPassword);
    }
    
    /**
     * Verify password
     */
    public boolean verifyPassword(String plainPassword, String hashedPassword) {
        return passwordEncoder.matches(plainPassword, hashedPassword);
    }
}