package com.smartcare.clinicmanagementsystem.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "medical_history")
public class MedicalHistory {
    
    @Id
    private String id;
    
    @NotNull(message = "Patient ID is required")
    @Field("patient_id")
    private Long patientId;
    
    @NotEmpty(message = "At least one medical record is required")
    private List<MedicalRecord> records;
    
    @Field("created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Field("updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();
    
    // Constructors
    public MedicalHistory() {}
    
    public MedicalHistory(Long patientId, List<MedicalRecord> records) {
        this.patientId = patientId;
        this.records = records;
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
    
    public List<MedicalRecord> getRecords() {
        return records;
    }
    
    public void setRecords(List<MedicalRecord> records) {
        this.records = records;
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
        return "MedicalHistory{" +
                "id='" + id + '\'' +
                ", patientId=" + patientId +
                ", recordsCount=" + (records != null ? records.size() : 0) +
                ", createdAt=" + createdAt +
                '}';
    }
}