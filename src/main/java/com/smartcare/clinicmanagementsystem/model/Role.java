package com.smartcare.clinicmanagementsystem.model;

public enum Role {
    ADMIN("Admin"),
    DOCTOR("Doctor"),
    PATIENT("Patient");
    
    private final String displayName;
    
    Role(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}