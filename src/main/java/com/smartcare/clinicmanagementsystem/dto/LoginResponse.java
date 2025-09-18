package com.smartcare.clinicmanagementsystem.dto;

import java.util.Map;

/**
 * Login Response DTO
 * Contains authentication token and user information
 */
public class LoginResponse {
    
    private String token;
    private String tokenType = "Bearer";
    private Long expiresIn;
    private Map<String, Object> user;
    
    // Constructors
    public LoginResponse() {}
    
    public LoginResponse(String token, Map<String, Object> user, Long expiresIn) {
        this.token = token;
        this.user = user;
        this.expiresIn = expiresIn;
    }
    
    // Getters and Setters
    public String getToken() {
        return token;
    }
    
    public void setToken(String token) {
        this.token = token;
    }
    
    public String getTokenType() {
        return tokenType;
    }
    
    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }
    
    public Long getExpiresIn() {
        return expiresIn;
    }
    
    public void setExpiresIn(Long expiresIn) {
        this.expiresIn = expiresIn;
    }
    
    public Map<String, Object> getUser() {
        return user;
    }
    
    public void setUser(Map<String, Object> user) {
        this.user = user;
    }
    
    @Override
    public String toString() {
        return "LoginResponse{" +
                "tokenType='" + tokenType + '\'' +
                ", expiresIn=" + expiresIn +
                ", user=" + user +
                '}';
    }
}