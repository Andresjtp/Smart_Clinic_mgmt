package com.smartcare.clinicmanagementsystem.model;

public enum AppointmentType {
    CONSULTATION("Consultation"),
    FOLLOW_UP("Follow-up"),
    CHECK_UP("Check-up"),
    EMERGENCY("Emergency"),
    PROCEDURE("Procedure"),
    VACCINATION("Vaccination"),
    SCREENING("Screening"),
    THERAPY("Therapy");
    
    private final String displayName;
    
    AppointmentType(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}