package com.smartcare.clinicmanagementsystem.controller;

import com.smartcare.clinicmanagementsystem.model.Prescription;
import com.smartcare.clinicmanagementsystem.model.User;
import com.smartcare.clinicmanagementsystem.service.PrescriptionService;
import com.smartcare.clinicmanagementsystem.service.UserService;
import com.smartcare.clinicmanagementsystem.dto.ApiResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Prescription Controller
 * Handles prescription management operations for doctors and patients
 */
@RestController
@RequestMapping("/api/prescriptions")
@CrossOrigin(origins = "*", maxAge = 3600)
public class PrescriptionController {

    @Autowired
    private PrescriptionService prescriptionService;
    
    @Autowired
    private UserService userService;

    /**
     * Create new prescription (Doctor only)
     */
    @PostMapping
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> createPrescription(
            @Valid @RequestBody Map<String, Object> prescriptionData) {
        try {
            Long patientId = Long.valueOf(prescriptionData.get("patientId").toString());
            Long doctorId = Long.valueOf(prescriptionData.get("doctorId").toString());
            String medicationName = prescriptionData.get("medicationName").toString();
            String dosage = prescriptionData.get("dosage").toString();
            String frequency = prescriptionData.get("frequency").toString();
            String duration = prescriptionData.get("duration").toString();
            String instructions = prescriptionData.containsKey("instructions") ? 
                prescriptionData.get("instructions").toString() : null;

            Prescription prescription = prescriptionService.createPrescription(
                patientId, doctorId, medicationName, dosage, frequency, duration, instructions);

            return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Prescription created successfully", 
                    convertToPrescriptionDto(prescription)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Error creating prescription: " + e.getMessage()));
        }
    }

    /**
     * Get all prescriptions (Admin only)
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getAllPrescriptions() {
        try {
            List<Prescription> prescriptions = prescriptionService.getAllPrescriptions();
            List<Map<String, Object>> prescriptionDtos = prescriptions.stream()
                .map(this::convertToPrescriptionDto)
                .collect(Collectors.toList());

            return ResponseEntity.ok(ApiResponse.success("Prescriptions retrieved successfully", 
                prescriptionDtos));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Error retrieving prescriptions: " + e.getMessage()));
        }
    }

    /**
     * Get prescription by ID
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('DOCTOR') or hasRole('PATIENT') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getPrescriptionById(@PathVariable Long id) {
        try {
            Optional<Prescription> prescriptionOpt = prescriptionService.getPrescriptionById(id);
            if (!prescriptionOpt.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Prescription not found"));
            }

            return ResponseEntity.ok(ApiResponse.success("Prescription retrieved successfully", 
                convertToPrescriptionDto(prescriptionOpt.get())));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Error retrieving prescription: " + e.getMessage()));
        }
    }

    /**
     * Get prescriptions by patient
     */
    @GetMapping("/patient/{patientId}")
    @PreAuthorize("hasRole('DOCTOR') or hasRole('PATIENT') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getPrescriptionsByPatient(
            @PathVariable Long patientId) {
        try {
            List<Prescription> prescriptions = prescriptionService.getPrescriptionsByPatient(patientId);
            List<Map<String, Object>> prescriptionDtos = prescriptions.stream()
                .map(this::convertToPrescriptionDto)
                .collect(Collectors.toList());

            return ResponseEntity.ok(ApiResponse.success("Patient prescriptions retrieved successfully", 
                prescriptionDtos));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Error retrieving prescriptions: " + e.getMessage()));
        }
    }

    /**
     * Get prescriptions by doctor
     */
    @GetMapping("/doctor/{doctorId}")
    @PreAuthorize("hasRole('DOCTOR') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getPrescriptionsByDoctor(
            @PathVariable Long doctorId) {
        try {
            List<Prescription> prescriptions = prescriptionService.getPrescriptionsByDoctor(doctorId);
            List<Map<String, Object>> prescriptionDtos = prescriptions.stream()
                .map(this::convertToPrescriptionDto)
                .collect(Collectors.toList());

            return ResponseEntity.ok(ApiResponse.success("Doctor prescriptions retrieved successfully", 
                prescriptionDtos));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Error retrieving prescriptions: " + e.getMessage()));
        }
    }

    /**
     * Get active prescriptions for patient
     */
    @GetMapping("/patient/{patientId}/active")
    @PreAuthorize("hasRole('DOCTOR') or hasRole('PATIENT') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getActivePrescriptionsByPatient(
            @PathVariable Long patientId) {
        try {
            List<Prescription> prescriptions = prescriptionService.getActivePrescriptionsByPatient(patientId);
            List<Map<String, Object>> prescriptionDtos = prescriptions.stream()
                .map(this::convertToPrescriptionDto)
                .collect(Collectors.toList());

            return ResponseEntity.ok(ApiResponse.success("Active prescriptions retrieved successfully", 
                prescriptionDtos));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Error retrieving active prescriptions: " + e.getMessage()));
        }
    }

    /**
     * Update prescription
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> updatePrescription(
            @PathVariable Long id, @Valid @RequestBody Map<String, Object> prescriptionData) {
        try {
            String medicationName = prescriptionData.containsKey("medicationName") ? 
                prescriptionData.get("medicationName").toString() : null;
            String dosage = prescriptionData.containsKey("dosage") ? 
                prescriptionData.get("dosage").toString() : null;
            String frequency = prescriptionData.containsKey("frequency") ? 
                prescriptionData.get("frequency").toString() : null;
            String duration = prescriptionData.containsKey("duration") ? 
                prescriptionData.get("duration").toString() : null;
            String instructions = prescriptionData.containsKey("instructions") ? 
                prescriptionData.get("instructions").toString() : null;

            Prescription prescription = prescriptionService.updatePrescription(
                id, medicationName, dosage, frequency, duration, instructions);

            return ResponseEntity.ok(ApiResponse.success("Prescription updated successfully", 
                convertToPrescriptionDto(prescription)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Error updating prescription: " + e.getMessage()));
        }
    }

    /**
     * Mark prescription as completed/discontinued
     */
    @PutMapping("/{id}/complete")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> completePrescription(
            @PathVariable Long id, @RequestBody(required = false) Map<String, String> completionData) {
        try {
            String reason = completionData != null ? completionData.get("reason") : null;
            Prescription prescription = prescriptionService.completePrescription(id, reason);

            return ResponseEntity.ok(ApiResponse.success("Prescription marked as completed", 
                convertToPrescriptionDto(prescription)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Error completing prescription: " + e.getMessage()));
        }
    }

    /**
     * Delete prescription (Admin only)
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deletePrescription(@PathVariable Long id) {
        try {
            prescriptionService.deletePrescription(id);
            return ResponseEntity.ok(ApiResponse.success("Prescription deleted successfully", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Error deleting prescription: " + e.getMessage()));
        }
    }

    /**
     * Get prescription statistics
     */
    @GetMapping("/stats")
    @PreAuthorize("hasRole('ADMIN') or hasRole('DOCTOR')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getPrescriptionStats() {
        try {
            Map<String, Object> stats = new HashMap<>();
            
            stats.put("totalPrescriptions", prescriptionService.getTotalPrescriptionsCount());
            stats.put("activePrescriptions", prescriptionService.getActivePrescriptionsCount());
            stats.put("completedPrescriptions", prescriptionService.getCompletedPrescriptionsCount());
            stats.put("todayPrescriptions", prescriptionService.getTodayPrescriptionsCount());
            
            return ResponseEntity.ok(ApiResponse.success("Prescription statistics retrieved", stats));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Error retrieving prescription statistics: " + e.getMessage()));
        }
    }

    /**
     * Search prescriptions by medication name
     */
    @GetMapping("/search")
    @PreAuthorize("hasRole('DOCTOR') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> searchPrescriptions(
            @RequestParam String medication) {
        try {
            List<Prescription> prescriptions = prescriptionService.searchPrescriptionsByMedication(medication);
            List<Map<String, Object>> prescriptionDtos = prescriptions.stream()
                .map(this::convertToPrescriptionDto)
                .collect(Collectors.toList());

            return ResponseEntity.ok(ApiResponse.success("Search completed", prescriptionDtos));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Error searching prescriptions: " + e.getMessage()));
        }
    }

    // ==================== HELPER METHODS ====================

    /**
     * Convert Prescription entity to DTO
     */
    private Map<String, Object> convertToPrescriptionDto(Prescription prescription) {
        Map<String, Object> prescriptionDto = new HashMap<>();
        prescriptionDto.put("id", prescription.getId());
        prescriptionDto.put("medicationName", prescription.getMedication() != null ? 
            prescription.getMedication().getName() : "N/A");
        prescriptionDto.put("dosage", prescription.getDosage());
        prescriptionDto.put("frequency", prescription.getFrequency());
        prescriptionDto.put("duration", prescription.getDuration());
        prescriptionDto.put("instructions", prescription.getInstructions());
        prescriptionDto.put("startDate", prescription.getStartDate());
        prescriptionDto.put("endDate", prescription.getEndDate());
        prescriptionDto.put("isActive", prescription.getIsActive());
        prescriptionDto.put("createdAt", prescription.getCreatedAt());
        prescriptionDto.put("updatedAt", prescription.getUpdatedAt());

        // Add patient information
        if (prescription.getPatient() != null && prescription.getPatient().getUser() != null) {
            Map<String, Object> patientInfo = new HashMap<>();
            patientInfo.put("id", prescription.getPatient().getId());
            patientInfo.put("name", prescription.getPatient().getUser().getFirstName() + " " + 
                           prescription.getPatient().getUser().getLastName());
            patientInfo.put("email", prescription.getPatient().getUser().getEmail());
            prescriptionDto.put("patient", patientInfo);
        }

        // Add doctor information
        if (prescription.getDoctor() != null && prescription.getDoctor().getUser() != null) {
            Map<String, Object> doctorInfo = new HashMap<>();
            doctorInfo.put("id", prescription.getDoctor().getId());
            doctorInfo.put("name", prescription.getDoctor().getUser().getFirstName() + " " + 
                          prescription.getDoctor().getUser().getLastName());
            doctorInfo.put("specialization", prescription.getDoctor().getSpecialization());
            prescriptionDto.put("doctor", doctorInfo);
        }

        return prescriptionDto;
    }
}