package com.smartcare.clinicmanagementsystem.service;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Token Service
 * Handles JWT token creation, validation, and management operations
 */
@Service
public class TokenService {

    private static final Logger logger = LoggerFactory.getLogger(TokenService.class);

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Value("${app.jwt.expiration}")
    private int jwtExpirationMs;

    @Value("${app.jwt.refresh-expiration}")
    private int jwtRefreshExpirationMs;

    /**
     * Generate JWT token for user
     */
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, userDetails.getUsername(), jwtExpirationMs);
    }

    /**
     * Generate JWT token with custom claims
     */
    public String generateToken(UserDetails userDetails, Map<String, Object> extraClaims) {
        Map<String, Object> claims = new HashMap<>(extraClaims);
        return createToken(claims, userDetails.getUsername(), jwtExpirationMs);
    }

    /**
     * Generate refresh token
     */
    public String generateRefreshToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("type", "refresh");
        return createToken(claims, userDetails.getUsername(), jwtRefreshExpirationMs);
    }

    /**
     * Generate token with custom expiration
     */
    public String generateTokenWithExpiration(UserDetails userDetails, int expirationMs) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, userDetails.getUsername(), expirationMs);
    }

    /**
     * Create JWT token
     */
    private String createToken(Map<String, Object> claims, String subject, int expirationMs) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationMs);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    /**
     * Extract username from token
     */
    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    /**
     * Extract expiration date from token
     */
    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    /**
     * Extract issued at date from token
     */
    public Date getIssuedAtDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getIssuedAt);
    }

    /**
     * Extract specific claim from token
     */
    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Extract all claims from token
     */
    private Claims getAllClaimsFromToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            logger.error("JWT token is expired: {}", e.getMessage());
            throw e;
        } catch (UnsupportedJwtException e) {
            logger.error("JWT token is unsupported: {}", e.getMessage());
            throw e;
        } catch (MalformedJwtException e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
            throw e;
        } catch (SignatureException e) {
            logger.error("Invalid JWT signature: {}", e.getMessage());
            throw e;
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Check if token is expired
     */
    public boolean isTokenExpired(String token) {
        try {
            final Date expiration = getExpirationDateFromToken(token);
            return expiration.before(new Date());
        } catch (ExpiredJwtException e) {
            return true;
        }
    }

    /**
     * Validate token against user details
     */
    public boolean validateToken(String token, UserDetails userDetails) {
        try {
            final String username = getUsernameFromToken(token);
            return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
        } catch (Exception e) {
            logger.error("Token validation failed: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Validate token structure and signature
     */
    public boolean validateTokenStructure(String token) {
        try {
            getAllClaimsFromToken(token);
            return true;
        } catch (Exception e) {
            logger.error("Token structure validation failed: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Check if token can be refreshed
     */
    public boolean canTokenBeRefreshed(String token) {
        try {
            final Date expiration = getExpirationDateFromToken(token);
            final Date issuedAt = getIssuedAtDateFromToken(token);
            
            // Allow refresh if token was issued within the last 30 days
            long thirtyDaysMs = 30L * 24 * 60 * 60 * 1000;
            Date thirtyDaysAgo = new Date(System.currentTimeMillis() - thirtyDaysMs);
            
            return issuedAt.after(thirtyDaysAgo);
        } catch (Exception e) {
            logger.error("Token refresh check failed: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Refresh token if eligible
     */
    public String refreshToken(String token, UserDetails userDetails) {
        if (canTokenBeRefreshed(token)) {
            return generateToken(userDetails);
        }
        throw new IllegalArgumentException("Token cannot be refreshed");
    }

    /**
     * Extract token type (access or refresh)
     */
    public String getTokenType(String token) {
        try {
            Claims claims = getAllClaimsFromToken(token);
            return claims.get("type", String.class);
        } catch (Exception e) {
            return "access"; // Default to access token
        }
    }

    /**
     * Check if token is a refresh token
     */
    public boolean isRefreshToken(String token) {
        return "refresh".equals(getTokenType(token));
    }

    /**
     * Get remaining time until token expiration (in milliseconds)
     */
    public long getTokenRemainingTime(String token) {
        try {
            Date expiration = getExpirationDateFromToken(token);
            return expiration.getTime() - System.currentTimeMillis();
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * Check if token will expire within specified minutes
     */
    public boolean willTokenExpireSoon(String token, int minutes) {
        try {
            long remainingTime = getTokenRemainingTime(token);
            long thresholdMs = minutes * 60 * 1000L;
            return remainingTime <= thresholdMs && remainingTime > 0;
        } catch (Exception e) {
            return true; // Consider expired/invalid tokens as expiring soon
        }
    }

    /**
     * Extract custom claim from token
     */
    public Object getCustomClaim(String token, String claimName) {
        try {
            Claims claims = getAllClaimsFromToken(token);
            return claims.get(claimName);
        } catch (Exception e) {
            logger.error("Failed to extract custom claim '{}': {}", claimName, e.getMessage());
            return null;
        }
    }

    /**
     * Get JWT signing key
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = jwtSecret.getBytes();
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Get token expiration time in milliseconds
     */
    public int getTokenExpirationMs() {
        return jwtExpirationMs;
    }

    /**
     * Get refresh token expiration time in milliseconds
     */
    public int getRefreshTokenExpirationMs() {
        return jwtRefreshExpirationMs;
    }

    /**
     * Create token with role-based claims
     */
    public String generateTokenWithRoles(UserDetails userDetails, String... roles) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", roles);
        claims.put("authorities", userDetails.getAuthorities());
        return createToken(claims, userDetails.getUsername(), jwtExpirationMs);
    }

    /**
     * Generate password reset token
     */
    public String generatePasswordResetToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("type", "password-reset");
        claims.put("purpose", "reset");
        
        // Password reset tokens expire in 1 hour
        int resetTokenExpiration = 60 * 60 * 1000; // 1 hour
        return createToken(claims, username, resetTokenExpiration);
    }

    /**
     * Generate email verification token
     */
    public String generateEmailVerificationToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("type", "email-verification");
        claims.put("purpose", "verify-email");
        
        // Email verification tokens expire in 24 hours
        int verificationTokenExpiration = 24 * 60 * 60 * 1000; // 24 hours
        return createToken(claims, username, verificationTokenExpiration);
    }

    /**
     * Validate password reset token
     */
    public boolean isValidPasswordResetToken(String token) {
        try {
            String tokenType = getTokenType(token);
            String purpose = (String) getCustomClaim(token, "purpose");
            return "password-reset".equals(tokenType) && "reset".equals(purpose) && !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Validate email verification token
     */
    public boolean isValidEmailVerificationToken(String token) {
        try {
            String tokenType = getTokenType(token);
            String purpose = (String) getCustomClaim(token, "purpose");
            return "email-verification".equals(tokenType) && "verify-email".equals(purpose) && !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }
}