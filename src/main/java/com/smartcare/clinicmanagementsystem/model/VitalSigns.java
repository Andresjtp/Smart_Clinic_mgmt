package com.smartcare.clinicmanagementsystem.model;

import jakarta.validation.constraints.*;

public class VitalSigns {
    
    @DecimalMin(value = "80.0", message = "Systolic pressure must be at least 80")
    @DecimalMax(value = "250.0", message = "Systolic pressure cannot exceed 250")
    private Double systolicPressure;
    
    @DecimalMin(value = "40.0", message = "Diastolic pressure must be at least 40")
    @DecimalMax(value = "150.0", message = "Diastolic pressure cannot exceed 150")
    private Double diastolicPressure;
    
    @DecimalMin(value = "30.0", message = "Heart rate must be at least 30 BPM")
    @DecimalMax(value = "220.0", message = "Heart rate cannot exceed 220 BPM")
    private Double heartRate;
    
    @DecimalMin(value = "10.0", message = "Respiratory rate must be at least 10")
    @DecimalMax(value = "60.0", message = "Respiratory rate cannot exceed 60")
    private Double respiratoryRate;
    
    @DecimalMin(value = "30.0", message = "Temperature must be at least 30°C")
    @DecimalMax(value = "45.0", message = "Temperature cannot exceed 45°C")
    private Double temperature;
    
    @DecimalMin(value = "85.0", message = "Oxygen saturation must be at least 85%")
    @DecimalMax(value = "100.0", message = "Oxygen saturation cannot exceed 100%")
    private Double oxygenSaturation;
    
    @DecimalMin(value = "20.0", message = "Weight must be at least 20 kg")
    @DecimalMax(value = "300.0", message = "Weight cannot exceed 300 kg")
    private Double weight;
    
    @DecimalMin(value = "50.0", message = "Height must be at least 50 cm")
    @DecimalMax(value = "250.0", message = "Height cannot exceed 250 cm")
    private Double height;
    
    @DecimalMin(value = "10.0", message = "BMI must be at least 10")
    @DecimalMax(value = "60.0", message = "BMI cannot exceed 60")
    private Double bmi;
    
    // Constructors
    public VitalSigns() {}
    
    public VitalSigns(Double systolicPressure, Double diastolicPressure, Double heartRate, 
                     Double respiratoryRate, Double temperature) {
        this.systolicPressure = systolicPressure;
        this.diastolicPressure = diastolicPressure;
        this.heartRate = heartRate;
        this.respiratoryRate = respiratoryRate;
        this.temperature = temperature;
    }
    
    // Getters and Setters
    public Double getSystolicPressure() {
        return systolicPressure;
    }
    
    public void setSystolicPressure(Double systolicPressure) {
        this.systolicPressure = systolicPressure;
    }
    
    public Double getDiastolicPressure() {
        return diastolicPressure;
    }
    
    public void setDiastolicPressure(Double diastolicPressure) {
        this.diastolicPressure = diastolicPressure;
    }
    
    public Double getHeartRate() {
        return heartRate;
    }
    
    public void setHeartRate(Double heartRate) {
        this.heartRate = heartRate;
    }
    
    public Double getRespiratoryRate() {
        return respiratoryRate;
    }
    
    public void setRespiratoryRate(Double respiratoryRate) {
        this.respiratoryRate = respiratoryRate;
    }
    
    public Double getTemperature() {
        return temperature;
    }
    
    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }
    
    public Double getOxygenSaturation() {
        return oxygenSaturation;
    }
    
    public void setOxygenSaturation(Double oxygenSaturation) {
        this.oxygenSaturation = oxygenSaturation;
    }
    
    public Double getWeight() {
        return weight;
    }
    
    public void setWeight(Double weight) {
        this.weight = weight;
    }
    
    public Double getHeight() {
        return height;
    }
    
    public void setHeight(Double height) {
        this.height = height;
    }
    
    public Double getBmi() {
        return bmi;
    }
    
    public void setBmi(Double bmi) {
        this.bmi = bmi;
    }
    
    // Calculate BMI if height and weight are available
    public void calculateBmi() {
        if (height != null && weight != null && height > 0) {
            double heightInMeters = height / 100.0; // Convert cm to meters
            this.bmi = Math.round((weight / (heightInMeters * heightInMeters)) * 100.0) / 100.0;
        }
    }
    
    @Override
    public String toString() {
        return "VitalSigns{" +
                "systolicPressure=" + systolicPressure +
                ", diastolicPressure=" + diastolicPressure +
                ", heartRate=" + heartRate +
                ", respiratoryRate=" + respiratoryRate +
                ", temperature=" + temperature +
                ", oxygenSaturation=" + oxygenSaturation +
                ", weight=" + weight +
                ", height=" + height +
                ", bmi=" + bmi +
                '}';
    }
}