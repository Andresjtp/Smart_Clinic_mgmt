package com.smartcare.clinicmanagementsystem.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "prescriptions")
public class Prescription {
    
    @Id
    private String id;
    
    @NotNull(message = "Patient ID is required")
    @Field("patient_id")
    private Long patientId;
    
    @NotNull(message = "Doctor ID is required")
    @Field("doctor_id")
    private Long doctorId;
    
    @Field("appointment_id")
    private Long appointmentId;
    
    @NotNull(message = "Prescription date is required")
    @Field("prescription_date")
    private LocalDate prescriptionDate;
    
    @NotEmpty(message = "At least one medication is required")
    private List<Medication> medications;
    
    @Size(max = 1000, message = "Instructions cannot exceed 1000 characters")
    private String instructions;
    
    @Size(max = 500, message = "Notes cannot exceed 500 characters")
    private String notes;
    
    @Field("is_active")
    private Boolean isActive = true;
    
    @Field("created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Field("updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();
    
    // Constructors
    public Prescription() {}
    
    public Prescription(Long patientId, Long doctorId, LocalDate prescriptionDate, List<Medication> medications) {
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.prescriptionDate = prescriptionDate;
        this.medications = medications;
    }
    
    // Getters and Setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public Long getPatientId() {
        return patientId;
    }
    
    public void setPatientId(Long patientId) {
        this.patientId = patientId;
    }
    
    public Long getDoctorId() {
        return doctorId;
    }
    
    public void setDoctorId(Long doctorId) {
        this.doctorId = doctorId;
    }
    
    public Long getAppointmentId() {
        return appointmentId;
    }
    
    public void setAppointmentId(Long appointmentId) {
        this.appointmentId = appointmentId;
    }
    
    public LocalDate getPrescriptionDate() {
        return prescriptionDate;
    }
    
    public void setPrescriptionDate(LocalDate prescriptionDate) {
        this.prescriptionDate = prescriptionDate;
    }
    
    public List<Medication> getMedications() {
        return medications;
    }
    
    public void setMedications(List<Medication> medications) {
        this.medications = medications;
    }
    
    public String getInstructions() {
        return instructions;
    }
    
    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    public Boolean getIsActive() {
        return isActive;
    }
    
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    @Override
    public String toString() {
        return "Prescription{" +
                "id='" + id + '\'' +
                ", patientId=" + patientId +
                ", doctorId=" + doctorId +
                ", prescriptionDate=" + prescriptionDate +
                ", medicationsCount=" + (medications != null ? medications.size() : 0) +
                ", isActive=" + isActive +
                '}';
    }
}