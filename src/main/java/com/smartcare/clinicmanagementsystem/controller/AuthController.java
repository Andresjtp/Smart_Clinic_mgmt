package com.smartcare.clinicmanagementsystem.controller;

import com.smartcare.clinicmanagementsystem.model.User;
import com.smartcare.clinicmanagementsystem.service.UserService;
import com.smartcare.clinicmanagementsystem.service.AuthenticationService;
import com.smartcare.clinicmanagementsystem.dto.LoginRequest;
import com.smartcare.clinicmanagementsystem.dto.LoginResponse;
import com.smartcare.clinicmanagementsystem.dto.ApiResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.Map;

/**
 * Authentication Controller
 * Handles user authentication, login, logout, and role-based routing
 */
@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationService authenticationService;

    // ==================== VIEW CONTROLLERS ====================

    /**
     * Display login page
     */
    @GetMapping("/login")
    public String showLoginPage() {
        return "auth/login";
    }

    /**
     * Display role selection page (landing page)
     */
    @GetMapping("/")
    public String showHomePage() {
        return "index";
    }

    /**
     * Logout and redirect to login page
     */
    @GetMapping("/logout")
    public String logout() {
        // In a real application, this would invalidate the session/JWT token
        return "redirect:/login?logout=true";
    }

    // ==================== API ENDPOINTS ====================

    /**
     * Authenticate user and return JWT token
     */
    @PostMapping("/api/auth/login")
    @ResponseBody
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            // Authenticate user
            User user = authenticationService.authenticate(loginRequest.getEmail(), loginRequest.getPassword());
            
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<>(false, "Invalid email or password", null));
            }

            // Generate JWT token (mock implementation)
            String token = authenticationService.generateToken(user);
            
            // Prepare response
            LoginResponse loginResponse = new LoginResponse();
            loginResponse.setToken(token);
            loginResponse.setUser(convertToUserDto(user));
            loginResponse.setExpiresIn(86400L); // 24 hours in seconds

            return ResponseEntity.ok(new ApiResponse<>(true, "Login successful", loginResponse));
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>(false, "Login failed: " + e.getMessage(), null));
        }
    }

    /**
     * Validate JWT token
     */
    @PostMapping("/api/auth/validate")
    @ResponseBody
    public ResponseEntity<ApiResponse<Map<String, Object>>> validateToken(@RequestHeader("Authorization") String token) {
        try {
            String jwtToken = token.replace("Bearer ", "");
            User user = authenticationService.validateToken(jwtToken);
            
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<>(false, "Invalid or expired token", null));
            }

            Map<String, Object> response = new HashMap<>();
            response.put("user", convertToUserDto(user));
            response.put("valid", true);

            return ResponseEntity.ok(new ApiResponse<>(true, "Token is valid", response));
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ApiResponse<>(false, "Token validation failed", null));
        }
    }

    /**
     * Refresh JWT token
     */
    @PostMapping("/api/auth/refresh")
    @ResponseBody
    public ResponseEntity<ApiResponse<LoginResponse>> refreshToken(@RequestHeader("Authorization") String token) {
        try {
            String jwtToken = token.replace("Bearer ", "");
            String newToken = authenticationService.refreshToken(jwtToken);
            
            if (newToken == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<>(false, "Unable to refresh token", null));
            }

            User user = authenticationService.validateToken(newToken);
            
            LoginResponse loginResponse = new LoginResponse();
            loginResponse.setToken(newToken);
            loginResponse.setUser(convertToUserDto(user));
            loginResponse.setExpiresIn(86400L);

            return ResponseEntity.ok(new ApiResponse<>(true, "Token refreshed", loginResponse));
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>(false, "Token refresh failed", null));
        }
    }

    // ==================== ROLE-BASED REDIRECTS ====================

    /**
     * Redirect to appropriate dashboard based on user role
     */
    @GetMapping("/dashboard")
    public String redirectToDashboard(@RequestParam(required = false) String role) {
        if (role == null) {
            return "redirect:/login";
        }

        switch (role.toUpperCase()) {
            case "ADMIN":
                return "redirect:/admin/dashboard";
            case "DOCTOR":
                return "redirect:/doctor/patient-records";
            case "PATIENT":
                return "redirect:/patient/doctors";
            default:
                return "redirect:/";
        }
    }

    // ==================== HELPER METHODS ====================

    /**
     * Convert User entity to DTO for API response
     */
    private Map<String, Object> convertToUserDto(User user) {
        Map<String, Object> userDto = new HashMap<>();
        userDto.put("id", user.getId());
        userDto.put("firstName", user.getFirstName());
        userDto.put("lastName", user.getLastName());
        userDto.put("email", user.getEmail());
        userDto.put("username", user.getUsername());
        userDto.put("role", user.getRole().name());
        userDto.put("active", user.getActive());
        
        return userDto;
    }
}