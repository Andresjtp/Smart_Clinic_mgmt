package com.smartcare.clinicmanagementsystem.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * JWT Utility Service
 * Handles JWT token generation, validation, and parsing
 */
@Component
public class JwtService {

    private static final Logger logger = LoggerFactory.getLogger(JwtService.class);

    @Value("${app.jwt.secret:mySecretKey}")
    private String jwtSecret;

    @Value("${app.jwt.expiration:86400}")
    private int jwtExpirationInSeconds;

    @Value("${app.jwt.refresh-expiration:604800}")
    private int refreshExpirationInSeconds;

    /**
     * Generate JWT token for user
     */
    public String generateToken(String username, String role, Long userId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", role);
        claims.put("userId", userId);
        return createToken(claims, username);
    }

    /**
     * Generate refresh token
     */
    public String generateRefreshToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("tokenType", "refresh");
        return createRefreshToken(claims, username);
    }

    /**
     * Create JWT token with claims
     */
    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationInSeconds * 1000L))
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Create refresh token
     */
    private String createRefreshToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + refreshExpirationInSeconds * 1000L))
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Get signing key
     */
    private Key getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Extract username from token
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extract user ID from token
     */
    public Long extractUserId(String token) {
        Claims claims = extractAllClaims(token);
        Object userId = claims.get("userId");
        if (userId instanceof Integer) {
            return ((Integer) userId).longValue();
        }
        return (Long) userId;
    }

    /**
     * Extract user role from token
     */
    public String extractRole(String token) {
        Claims claims = extractAllClaims(token);
        return (String) claims.get("role");
    }

    /**
     * Extract expiration date from token
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Extract specific claim from token
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Extract all claims from token
     */
    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSignKey())
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
        } catch (io.jsonwebtoken.security.SignatureException e) {
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
    private Boolean isTokenExpired(String token) {
        try {
            return extractExpiration(token).before(new Date());
        } catch (ExpiredJwtException e) {
            return true;
        }
    }

    /**
     * Validate token against username
     */
    public Boolean validateToken(String token, String username) {
        try {
            final String extractedUsername = extractUsername(token);
            return (extractedUsername.equals(username) && !isTokenExpired(token));
        } catch (Exception e) {
            logger.error("Token validation failed: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Check if token is valid
     */
    public Boolean isTokenValid(String token) {
        try {
            return !isTokenExpired(token);
        } catch (Exception e) {
            logger.error("Token validation failed: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Get token type
     */
    public String getTokenType(String token) {
        try {
            Claims claims = extractAllClaims(token);
            return (String) claims.get("tokenType");
        } catch (Exception e) {
            return "access"; // Default to access token
        }
    }

    /**
     * Check if token is refresh token
     */
    public Boolean isRefreshToken(String token) {
        return "refresh".equals(getTokenType(token));
    }

    /**
     * Get token expiration time in seconds
     */
    public int getJwtExpiration() {
        return jwtExpirationInSeconds;
    }

    /**
     * Get refresh token expiration time in seconds
     */
    public int getRefreshExpiration() {
        return refreshExpirationInSeconds;
    }
}