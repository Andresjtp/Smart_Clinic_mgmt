package com.smartcare.clinicmanagementsystem.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Patient Controller
 * Handles patient-specific pages and functions
 */
@Controller
@RequestMapping("/patient")
public class PatientController {

    /**
     * Display patient dashboard
     */
    @GetMapping("/dashboard")
    public String showPatientDashboard() {
        return "patient/dashboard";
    }

    /**
     * Display available doctors page
     */
    @GetMapping("/doctors")
    public String showDoctors() {
        return "patient/doctors";
    }

    /**
     * Display appointment booking page
     */
    @GetMapping("/book-appointment")
    public String showBookAppointment() {
        return "patient/book-appointment";
    }

    /**
     * Display patient's appointments
     */
    @GetMapping("/appointments")
    public String showPatientAppointments() {
        return "patient/appointments";
    }

    /**
     * Display patient home page
     */
    @GetMapping("/home")
    public String showPatientHome() {
        return "patient/doctors"; // Redirect to doctors list as main page
    }

    /**
     * Display patient medical history
     */
    @GetMapping("/medical-history")
    public String showMedicalHistory() {
        return "patient/medical-history";
    }

    /**
     * Display patient prescriptions
     */
    @GetMapping("/prescriptions")
    public String showPrescriptions() {
        return "patient/prescriptions";
    }

    /**
     * Display patient profile
     */
    @GetMapping("/profile")
    public String showPatientProfile() {
        return "patient/profile";
    }
}