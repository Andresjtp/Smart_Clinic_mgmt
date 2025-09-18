package com.smartcare.clinicmanagementsystem.security;

import com.smartcare.clinicmanagementsystem.service.UserService;
import com.smartcare.clinicmanagementsystem.model.User;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * JWT Authentication Filter
 * Intercepts requests and validates JWT tokens
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserService userService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String username;

        // Check if Authorization header is present and starts with "Bearer "
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Extract JWT token
        jwt = authHeader.substring(7);

        try {
            // Extract username from JWT token
            username = jwtService.extractUsername(jwt);

            // If username is present and no authentication is set in SecurityContext
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                
                // Load user from database
                User user = userService.findByEmail(username);
                
                if (user != null && jwtService.validateToken(jwt, username)) {
                    // Create authentication token
                    List<SimpleGrantedAuthority> authorities = Collections.singletonList(
                            new SimpleGrantedAuthority("ROLE_" + user.getRole().name())
                    );
                    
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            user, 
                            null, 
                            authorities
                    );
                    
                    // Set authentication details
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    
                    // Set authentication in SecurityContext
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (Exception e) {
            logger.error("Cannot set user authentication: {}", e.getMessage());
        }

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        
        // Skip JWT validation for public endpoints
        return path.equals("/") ||
               path.equals("/login") ||
               path.equals("/register") ||
               path.equals("/forgot-password") ||
               path.startsWith("/api/auth/") ||
               path.startsWith("/css/") ||
               path.startsWith("/js/") ||
               path.startsWith("/images/") ||
               path.startsWith("/favicon.ico") ||
               path.startsWith("/error") ||
               path.startsWith("/actuator/health");
    }
}