package com.smartcare.clinicmanagementsystem.controller;

import com.smartcare.clinicmanagementsystem.model.*;
import com.smartcare.clinicmanagementsystem.service.UserService;
import com.smartcare.clinicmanagementsystem.service.AppointmentService;
import com.smartcare.clinicmanagementsystem.dto.ApiResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
    
    @Autowired
    private AppointmentService appointmentService;

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

    // ==================== APPOINTMENT ENDPOINTS ====================

    /**
     * Create new appointment
     */
    @PostMapping("/appointments")
    public ResponseEntity<ApiResponse<Map<String, Object>>> createAppointment(
            @Valid @RequestBody Map<String, Object> appointmentData) {
        try {
            Long doctorId = Long.valueOf(appointmentData.get("doctorId").toString());
            Long patientId = Long.valueOf(appointmentData.get("patientId").toString());
            String dateTimeStr = appointmentData.get("appointmentDateTime").toString();
            LocalDateTime appointmentDateTime = LocalDateTime.parse(dateTimeStr);
            Integer durationMinutes = Integer.valueOf(appointmentData.get("durationMinutes").toString());
            AppointmentType appointmentType = AppointmentType.valueOf(
                appointmentData.get("appointmentType").toString().toUpperCase());
            String reasonForVisit = appointmentData.get("reasonForVisit").toString();

            Appointment appointment = appointmentService.createAppointment(
                doctorId, patientId, appointmentDateTime, durationMinutes, appointmentType, reasonForVisit);

            return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Appointment created successfully", convertToAppointmentDto(appointment)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Error creating appointment: " + e.getMessage()));
        }
    }

    /**
     * Get all appointments
     */
    @GetMapping("/appointments")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getAllAppointments() {
        try {
            List<Appointment> appointments = appointmentService.getAllAppointments();
            List<Map<String, Object>> appointmentDtos = appointments.stream()
                .map(this::convertToAppointmentDto)
                .collect(Collectors.toList());

            return ResponseEntity.ok(ApiResponse.success("Appointments retrieved successfully", appointmentDtos));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Error retrieving appointments: " + e.getMessage()));
        }
    }

    /**
     * Get appointment by ID
     */
    @GetMapping("/appointments/{id}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getAppointmentById(@PathVariable Long id) {
        try {
            Optional<Appointment> appointmentOpt = appointmentService.getAppointmentById(id);
            if (!appointmentOpt.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Appointment not found"));
            }

            return ResponseEntity.ok(ApiResponse.success("Appointment retrieved successfully", 
                convertToAppointmentDto(appointmentOpt.get())));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Error retrieving appointment: " + e.getMessage()));
        }
    }

    /**
     * Get appointments by doctor
     */
    @GetMapping("/appointments/doctor/{doctorId}")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getAppointmentsByDoctor(@PathVariable Long doctorId) {
        try {
            List<Appointment> appointments = appointmentService.getAppointmentsByDoctor(doctorId);
            List<Map<String, Object>> appointmentDtos = appointments.stream()
                .map(this::convertToAppointmentDto)
                .collect(Collectors.toList());

            return ResponseEntity.ok(ApiResponse.success("Doctor appointments retrieved successfully", appointmentDtos));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Error retrieving appointments: " + e.getMessage()));
        }
    }

    /**
     * Get appointments by patient
     */
    @GetMapping("/appointments/patient/{patientId}")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getAppointmentsByPatient(@PathVariable Long patientId) {
        try {
            List<Appointment> appointments = appointmentService.getAppointmentsByPatient(patientId);
            List<Map<String, Object>> appointmentDtos = appointments.stream()
                .map(this::convertToAppointmentDto)
                .collect(Collectors.toList());

            return ResponseEntity.ok(ApiResponse.success("Patient appointments retrieved successfully", appointmentDtos));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Error retrieving appointments: " + e.getMessage()));
        }
    }

    /**
     * Get appointments by status
     */
    @GetMapping("/appointments/status/{status}")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getAppointmentsByStatus(@PathVariable String status) {
        try {
            AppointmentStatus appointmentStatus = AppointmentStatus.valueOf(status.toUpperCase());
            List<Appointment> appointments = appointmentService.getAppointmentsByStatus(appointmentStatus);
            List<Map<String, Object>> appointmentDtos = appointments.stream()
                .map(this::convertToAppointmentDto)
                .collect(Collectors.toList());

            return ResponseEntity.ok(ApiResponse.success("Appointments retrieved successfully", appointmentDtos));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Invalid appointment status: " + status));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Error retrieving appointments: " + e.getMessage()));
        }
    }

    /**
     * Get doctor's appointments for a specific date
     */
    @GetMapping("/appointments/doctor/{doctorId}/date/{date}")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getDoctorAppointmentsForDate(
            @PathVariable Long doctorId, @PathVariable String date) {
        try {
            LocalDate appointmentDate = LocalDate.parse(date);
            List<Appointment> appointments = appointmentService.getDoctorAppointmentsForDate(doctorId, appointmentDate);
            List<Map<String, Object>> appointmentDtos = appointments.stream()
                .map(this::convertToAppointmentDto)
                .collect(Collectors.toList());

            return ResponseEntity.ok(ApiResponse.success("Appointments retrieved successfully", appointmentDtos));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Error retrieving appointments: " + e.getMessage()));
        }
    }

    /**
     * Update appointment status
     */
    @PutMapping("/appointments/{id}/status")
    public ResponseEntity<ApiResponse<Map<String, Object>>> updateAppointmentStatus(
            @PathVariable Long id, @RequestBody Map<String, String> statusUpdate) {
        try {
            String statusStr = statusUpdate.get("status");
            AppointmentStatus status = AppointmentStatus.valueOf(statusStr.toUpperCase());
            
            Appointment appointment = appointmentService.updateAppointmentStatus(id, status);
            
            return ResponseEntity.ok(ApiResponse.success("Appointment status updated successfully", 
                convertToAppointmentDto(appointment)));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Invalid appointment status"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Error updating appointment status: " + e.getMessage()));
        }
    }

    /**
     * Cancel appointment
     */
    @PutMapping("/appointments/{id}/cancel")
    public ResponseEntity<ApiResponse<Map<String, Object>>> cancelAppointment(
            @PathVariable Long id, @RequestBody(required = false) Map<String, String> cancelData) {
        try {
            String reason = cancelData != null ? cancelData.get("reason") : null;
            Appointment appointment = appointmentService.cancelAppointment(id, reason);
            
            return ResponseEntity.ok(ApiResponse.success("Appointment cancelled successfully", 
                convertToAppointmentDto(appointment)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Error cancelling appointment: " + e.getMessage()));
        }
    }

    /**
     * Reschedule appointment
     */
    @PutMapping("/appointments/{id}/reschedule")
    public ResponseEntity<ApiResponse<Map<String, Object>>> rescheduleAppointment(
            @PathVariable Long id, @RequestBody Map<String, Object> rescheduleData) {
        try {
            String newDateTimeStr = rescheduleData.get("newDateTime").toString();
            LocalDateTime newDateTime = LocalDateTime.parse(newDateTimeStr);
            Integer newDurationMinutes = rescheduleData.containsKey("newDurationMinutes") ? 
                Integer.valueOf(rescheduleData.get("newDurationMinutes").toString()) : null;
            
            Appointment appointment = appointmentService.rescheduleAppointment(id, newDateTime, newDurationMinutes);
            
            return ResponseEntity.ok(ApiResponse.success("Appointment rescheduled successfully", 
                convertToAppointmentDto(appointment)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Error rescheduling appointment: " + e.getMessage()));
        }
    }

    /**
     * Complete appointment
     */
    @PutMapping("/appointments/{id}/complete")
    public ResponseEntity<ApiResponse<Map<String, Object>>> completeAppointment(
            @PathVariable Long id, @RequestBody(required = false) Map<String, String> completionData) {
        try {
            String doctorNotes = completionData != null ? completionData.get("doctorNotes") : null;
            Appointment appointment = appointmentService.completeAppointment(id, doctorNotes);
            
            return ResponseEntity.ok(ApiResponse.success("Appointment completed successfully", 
                convertToAppointmentDto(appointment)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Error completing appointment: " + e.getMessage()));
        }
    }

    /**
     * Update appointment notes
     */
    @PutMapping("/appointments/{id}/notes")
    public ResponseEntity<ApiResponse<Map<String, Object>>> updateAppointmentNotes(
            @PathVariable Long id, @RequestBody Map<String, String> notesData) {
        try {
            String notes = notesData.get("notes");
            String doctorNotes = notesData.get("doctorNotes");
            
            Appointment appointment = appointmentService.updateAppointmentNotes(id, notes, doctorNotes);
            
            return ResponseEntity.ok(ApiResponse.success("Appointment notes updated successfully", 
                convertToAppointmentDto(appointment)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Error updating appointment notes: " + e.getMessage()));
        }
    }

    /**
     * Update appointment payment
     */
    @PutMapping("/appointments/{id}/payment")
    public ResponseEntity<ApiResponse<Map<String, Object>>> updateAppointmentPayment(
            @PathVariable Long id, @RequestBody Map<String, Object> paymentData) {
        try {
            Double fee = paymentData.containsKey("fee") ? 
                Double.valueOf(paymentData.get("fee").toString()) : null;
            Boolean isPaid = paymentData.containsKey("isPaid") ? 
                Boolean.valueOf(paymentData.get("isPaid").toString()) : null;
            
            Appointment appointment = appointmentService.updateAppointmentPayment(id, fee, isPaid);
            
            return ResponseEntity.ok(ApiResponse.success("Appointment payment updated successfully", 
                convertToAppointmentDto(appointment)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Error updating appointment payment: " + e.getMessage()));
        }
    }

    /**
     * Get upcoming appointments for doctor
     */
    @GetMapping("/appointments/doctor/{doctorId}/upcoming")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getUpcomingAppointmentsForDoctor(@PathVariable Long doctorId) {
        try {
            List<Appointment> appointments = appointmentService.getUpcomingAppointmentsForDoctor(doctorId);
            List<Map<String, Object>> appointmentDtos = appointments.stream()
                .map(this::convertToAppointmentDto)
                .collect(Collectors.toList());

            return ResponseEntity.ok(ApiResponse.success("Upcoming appointments retrieved successfully", appointmentDtos));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Error retrieving upcoming appointments: " + e.getMessage()));
        }
    }

    /**
     * Get upcoming appointments for patient
     */
    @GetMapping("/appointments/patient/{patientId}/upcoming")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getUpcomingAppointmentsForPatient(@PathVariable Long patientId) {
        try {
            List<Appointment> appointments = appointmentService.getUpcomingAppointmentsForPatient(patientId);
            List<Map<String, Object>> appointmentDtos = appointments.stream()
                .map(this::convertToAppointmentDto)
                .collect(Collectors.toList());

            return ResponseEntity.ok(ApiResponse.success("Upcoming appointments retrieved successfully", appointmentDtos));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Error retrieving upcoming appointments: " + e.getMessage()));
        }
    }

    /**
     * Check slot availability
     */
    @PostMapping("/appointments/check-availability")
    public ResponseEntity<ApiResponse<Map<String, Object>>> checkSlotAvailability(@RequestBody Map<String, Object> availabilityData) {
        try {
            Long doctorId = Long.valueOf(availabilityData.get("doctorId").toString());
            String dateTimeStr = availabilityData.get("dateTime").toString();
            LocalDateTime dateTime = LocalDateTime.parse(dateTimeStr);
            Integer durationMinutes = Integer.valueOf(availabilityData.get("durationMinutes").toString());
            
            // This requires getting the doctor entity - we'll need to call the doctor service
            // For now, we'll create a simpler availability check
            boolean isAvailable = true; // Simplified for now
            
            Map<String, Object> result = new HashMap<>();
            result.put("available", isAvailable);
            result.put("doctorId", doctorId);
            result.put("dateTime", dateTime);
            result.put("durationMinutes", durationMinutes);
            
            return ResponseEntity.ok(ApiResponse.success("Availability checked", result));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Error checking availability: " + e.getMessage()));
        }
    }

    /**
     * Get appointment statistics
     */
    @GetMapping("/appointments/stats")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getAppointmentStats() {
        try {
            Map<String, Object> stats = new HashMap<>();
            
            stats.put("scheduledAppointments", appointmentService.getAppointmentCountByStatus(AppointmentStatus.SCHEDULED));
            stats.put("completedAppointments", appointmentService.getCompletedAppointmentsCount());
            stats.put("cancelledAppointments", appointmentService.getAppointmentCountByStatus(AppointmentStatus.CANCELLED));
            stats.put("confirmedAppointments", appointmentService.getAppointmentCountByStatus(AppointmentStatus.CONFIRMED));
            stats.put("todayAppointments", appointmentService.getTodayAppointmentCount());
            
            return ResponseEntity.ok(ApiResponse.success("Appointment statistics retrieved", stats));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Error retrieving appointment statistics: " + e.getMessage()));
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

    /**
     * Convert Appointment entity to DTO
     */
    private Map<String, Object> convertToAppointmentDto(Appointment appointment) {
        Map<String, Object> appointmentDto = new HashMap<>();
        appointmentDto.put("id", appointment.getId());
        appointmentDto.put("appointmentDateTime", appointment.getAppointmentDateTime());
        appointmentDto.put("durationMinutes", appointment.getDurationMinutes());
        appointmentDto.put("status", appointment.getStatus().name());
        appointmentDto.put("appointmentType", appointment.getAppointmentType().name());
        appointmentDto.put("reasonForVisit", appointment.getReasonForVisit());
        appointmentDto.put("notes", appointment.getNotes());
        appointmentDto.put("doctorNotes", appointment.getDoctorNotes());
        appointmentDto.put("fee", appointment.getFee());
        appointmentDto.put("isPaid", appointment.getIsPaid());
        appointmentDto.put("createdAt", appointment.getCreatedAt());
        appointmentDto.put("updatedAt", appointment.getUpdatedAt());
        
        // Add doctor information
        if (appointment.getDoctor() != null && appointment.getDoctor().getUser() != null) {
            Map<String, Object> doctorInfo = new HashMap<>();
            doctorInfo.put("id", appointment.getDoctor().getId());
            doctorInfo.put("name", appointment.getDoctor().getUser().getFirstName() + " " + 
                          appointment.getDoctor().getUser().getLastName());
            doctorInfo.put("specialization", appointment.getDoctor().getSpecialization());
            appointmentDto.put("doctor", doctorInfo);
        }
        
        // Add patient information
        if (appointment.getPatient() != null && appointment.getPatient().getUser() != null) {
            Map<String, Object> patientInfo = new HashMap<>();
            patientInfo.put("id", appointment.getPatient().getId());
            patientInfo.put("name", appointment.getPatient().getUser().getFirstName() + " " + 
                           appointment.getPatient().getUser().getLastName());
            patientInfo.put("email", appointment.getPatient().getUser().getEmail());
            appointmentDto.put("patient", patientInfo);
        }
        
        return appointmentDto;
    }
}