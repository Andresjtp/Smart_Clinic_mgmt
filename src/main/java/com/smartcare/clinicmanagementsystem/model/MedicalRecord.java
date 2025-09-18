package com.smartcare.clinicmanagementsystem.model;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.List;

public class MedicalRecord {
    
    @NotNull(message = "Record date is required")
    private LocalDateTime recordDate;
    
    @NotNull(message = "Record type is required")
    @Enumerated(EnumType.STRING)
    private RecordType recordType;
    
    @NotBlank(message = "Title is required")
    @Size(max = 200, message = "Title cannot exceed 200 characters")
    private String title;
    
    @NotBlank(message = "Description is required")
    @Size(max = 2000, message = "Description cannot exceed 2000 characters")
    private String description;
    
    @NotNull(message = "Doctor ID is required")
    private Long doctorId;
    
    private Long appointmentId;
    
    @Size(max = 500, message = "Diagnosis cannot exceed 500 characters")
    private String diagnosis;
    
    @Size(max = 500, message = "Treatment cannot exceed 500 characters")
    private String treatment;
    
    @Size(max = 1000, message = "Notes cannot exceed 1000 characters")
    private String notes;
    
    private List<String> attachments;
    
    private VitalSigns vitalSigns;
    
    private List<String> symptoms;
    
    @DecimalMin(value = "0.0", message = "Severity cannot be negative")
    @DecimalMax(value = "10.0", message = "Severity cannot exceed 10")
    private Double severity;
    
    private Boolean isConfidential = false;
    
    private LocalDateTime createdAt = LocalDateTime.now();
    
    // Constructors
    public MedicalRecord() {}
    
    public MedicalRecord(LocalDateTime recordDate, RecordType recordType, String title, 
                        String description, Long doctorId) {
        this.recordDate = recordDate;
        this.recordType = recordType;
        this.title = title;
        this.description = description;
        this.doctorId = doctorId;
    }
    
    // Getters and Setters
    public LocalDateTime getRecordDate() {
        return recordDate;
    }
    
    public void setRecordDate(LocalDateTime recordDate) {
        this.recordDate = recordDate;
    }
    
    public RecordType getRecordType() {
        return recordType;
    }
    
    public void setRecordType(RecordType recordType) {
        this.recordType = recordType;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
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
    
    public String getDiagnosis() {
        return diagnosis;
    }
    
    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }
    
    public String getTreatment() {
        return treatment;
    }
    
    public void setTreatment(String treatment) {
        this.treatment = treatment;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    public List<String> getAttachments() {
        return attachments;
    }
    
    public void setAttachments(List<String> attachments) {
        this.attachments = attachments;
    }
    
    public VitalSigns getVitalSigns() {
        return vitalSigns;
    }
    
    public void setVitalSigns(VitalSigns vitalSigns) {
        this.vitalSigns = vitalSigns;
    }
    
    public List<String> getSymptoms() {
        return symptoms;
    }
    
    public void setSymptoms(List<String> symptoms) {
        this.symptoms = symptoms;
    }
    
    public Double getSeverity() {
        return severity;
    }
    
    public void setSeverity(Double severity) {
        this.severity = severity;
    }
    
    public Boolean getIsConfidential() {
        return isConfidential;
    }
    
    public void setIsConfidential(Boolean isConfidential) {
        this.isConfidential = isConfidential;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    @Override
    public String toString() {
        return "MedicalRecord{" +
                "recordDate=" + recordDate +
                ", recordType=" + recordType +
                ", title='" + title + '\'' +
                ", doctorId=" + doctorId +
                ", severity=" + severity +
                ", isConfidential=" + isConfidential +
                '}';
    }
}