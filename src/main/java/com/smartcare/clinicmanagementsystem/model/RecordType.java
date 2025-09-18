package com.smartcare.clinicmanagementsystem.model;

public enum RecordType {
    CONSULTATION("Consultation"),
    DIAGNOSIS("Diagnosis"),
    TREATMENT("Treatment"),
    PRESCRIPTION("Prescription"),
    LAB_RESULT("Lab Result"),
    IMAGING("Imaging"),
    SURGERY("Surgery"),
    VACCINATION("Vaccination"),
    ALLERGY("Allergy"),
    CHRONIC_CONDITION("Chronic Condition"),
    EMERGENCY("Emergency"),
    FOLLOW_UP("Follow-up"),
    DISCHARGE("Discharge"),
    REFERRAL("Referral");
    
    private final String displayName;
    
    RecordType(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}