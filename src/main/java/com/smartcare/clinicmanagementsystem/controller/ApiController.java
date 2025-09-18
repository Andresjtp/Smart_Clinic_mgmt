package com.smartcare.clinicmanagementsystem.controller;

import com.smartcare.clinicmanagementsystem.model.User;
import com.smartcare.clinicmanagementsystem.model.Role;
import com.smartcare.clinicmanagementsystem.service.UserService;
import com.smartcare.clinicmanagementsystem.dto.ApiResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * API Controller
 * Handles API endpoints for data operations
 */
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ApiController {

    @Autowired
    private UserService userService;

    // ==================== USER ENDPOINTS ====================

    /**
     * Get all users
     */
    @GetMapping("/users")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getAllUsers() {
        try {
            List<User> users = userService.findAll();
            List<Map<String, Object>> userDtos = users.stream()
                    .map(this::convertToUserDto)
                    .collect(Collectors.toList());
            
            return ResponseEntity.ok(ApiResponse.success("Users retrieved successfully", userDtos));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error retrieving users: " + e.getMessage()));
        }
    }

    /**
     * Get user by ID
     */
    @GetMapping("/users/{id}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getUserById(@PathVariable Long id) {
        try {
            User user = userService.findById(id);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("User not found"));
            }
            
            return ResponseEntity.ok(ApiResponse.success("User retrieved successfully", convertToUserDto(user)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error retrieving user: " + e.getMessage()));
        }
    }

    /**
     * Get users by role
     */
    @GetMapping("/users/role/{role}")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getUsersByRole(@PathVariable String role) {
        try {
            Role userRole = Role.valueOf(role.toUpperCase());
            List<User> users = userService.findByRole(userRole);
            List<Map<String, Object>> userDtos = users.stream()
                    .map(this::convertToUserDto)
                    .collect(Collectors.toList());
            
            return ResponseEntity.ok(ApiResponse.success("Users retrieved successfully", userDtos));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Invalid role: " + role));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error retrieving users: " + e.getMessage()));
        }
    }

    // ==================== DASHBOARD DATA ENDPOINTS ====================

    /**
     * Get dashboard statistics
     */
    @GetMapping("/dashboard/stats")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getDashboardStats() {
        try {
            Map<String, Object> stats = new HashMap<>();
            
            // Count users by role
            List<User> allUsers = userService.findAll();
            long totalPatients = allUsers.stream().filter(u -> u.getRole() == Role.PATIENT).count();
            long totalDoctors = allUsers.stream().filter(u -> u.getRole() == Role.DOCTOR).count();
            long totalAdmins = allUsers.stream().filter(u -> u.getRole() == Role.ADMIN).count();
            
            stats.put("totalPatients", totalPatients);
            stats.put("totalDoctors", totalDoctors);
            stats.put("totalAdmins", totalAdmins);
            stats.put("totalUsers", allUsers.size());
            
            // Mock data for other stats
            stats.put("todayAppointments", 156);
            stats.put("monthlyRevenue", 124350);
            stats.put("activeUsers", allUsers.stream().filter(User::getActive).count());
            
            return ResponseEntity.ok(ApiResponse.success("Dashboard stats retrieved", stats));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error retrieving dashboard stats: " + e.getMessage()));
        }
    }

    /**
     * Get doctors for patient booking
     */
    @GetMapping("/doctors")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getDoctors() {
        try {
            List<User> doctors = userService.findByRole(Role.DOCTOR);
            List<Map<String, Object>> doctorDtos = doctors.stream()
                    .filter(User::getActive)
                    .map(this::convertToDoctorDto)
                    .collect(Collectors.toList());
            
            return ResponseEntity.ok(ApiResponse.success("Doctors retrieved successfully", doctorDtos));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error retrieving doctors: " + e.getMessage()));
        }
    }

    /**
     * Search users
     */
    @GetMapping("/users/search")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> searchUsers(
            @RequestParam String query,
            @RequestParam(required = false) String role) {
        try {
            List<User> users = userService.findAll();
            
            // Filter by role if specified
            if (role != null && !role.isEmpty()) {
                Role userRole = Role.valueOf(role.toUpperCase());
                users = users.stream().filter(u -> u.getRole() == userRole).collect(Collectors.toList());
            }
            
            // Filter by search query
            users = users.stream()
                    .filter(u -> u.getFirstName().toLowerCase().contains(query.toLowerCase()) ||
                               u.getLastName().toLowerCase().contains(query.toLowerCase()) ||
                               u.getEmail().toLowerCase().contains(query.toLowerCase()) ||
                               u.getUsername().toLowerCase().contains(query.toLowerCase()))
                    .collect(Collectors.toList());
            
            List<Map<String, Object>> userDtos = users.stream()
                    .map(this::convertToUserDto)
                    .collect(Collectors.toList());
            
            return ResponseEntity.ok(ApiResponse.success("Search completed", userDtos));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error searching users: " + e.getMessage()));
        }
    }

    // ==================== HELPER METHODS ====================

    /**
     * Convert User entity to basic DTO
     */
    private Map<String, Object> convertToUserDto(User user) {
        Map<String, Object> userDto = new HashMap<>();
        userDto.put("id", user.getId());
        userDto.put("username", user.getUsername());
        userDto.put("firstName", user.getFirstName());
        userDto.put("lastName", user.getLastName());
        userDto.put("email", user.getEmail());
        userDto.put("role", user.getRole().name());
        userDto.put("active", user.getActive());
        userDto.put("createdAt", user.getCreatedAt());
        userDto.put("updatedAt", user.getUpdatedAt());
        
        return userDto;
    }

    /**
     * Convert User entity to Doctor DTO
     */
    private Map<String, Object> convertToDoctorDto(User user) {
        Map<String, Object> doctorDto = new HashMap<>();
        doctorDto.put("id", user.getId());
        doctorDto.put("name", user.getFirstName() + " " + user.getLastName());
        doctorDto.put("firstName", user.getFirstName());
        doctorDto.put("lastName", user.getLastName());
        doctorDto.put("email", user.getEmail());
        
        // Mock specialization data (in real app, this would come from Doctor entity relationship)
        String[] specializations = {"Cardiology", "Neurology", "Orthopedics", "Pediatrics", "Internal Medicine"};
        doctorDto.put("specialization", specializations[Math.abs(user.getId().hashCode()) % specializations.length]);
        
        // Mock rating and availability
        doctorDto.put("rating", 4.5 + (Math.random() * 0.5));
        doctorDto.put("available", user.getActive());
        doctorDto.put("consultationFee", 150 + (Math.random() * 100));
        
        return doctorDto;
    }
}