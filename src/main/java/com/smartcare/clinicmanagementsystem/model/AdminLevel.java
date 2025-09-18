package com.smartcare.clinicmanagementsystem.model;

public enum AdminLevel {
    SUPER_ADMIN("Super Admin"),
    MANAGER("Manager"),
    STAFF("Staff");
    
    private final String displayName;
    
    AdminLevel(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}