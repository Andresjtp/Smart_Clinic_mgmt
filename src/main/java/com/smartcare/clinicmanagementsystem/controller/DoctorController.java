package com.smartcare.clinicmanagementsystem.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Doctor Controller
 * Handles doctor-specific pages and functions
 */
@Controller
@RequestMapping("/doctor")
public class DoctorController {

    /**
     * Display doctor dashboard
     */
    @GetMapping("/dashboard")
    public String showDoctorDashboard() {
        return "doctor/dashboard";
    }

    /**
     * Display patient records page
     */
    @GetMapping("/patient-records")
    public String showPatientRecords() {
        return "doctor/patient-records";
    }

    /**
     * Display doctor's appointments
     */
    @GetMapping("/appointments")
    public String showDoctorAppointments() {
        return "doctor/appointments";
    }

    /**
     * Display doctor home/profile page
     */
    @GetMapping("/home")
    public String showDoctorHome() {
        return "doctor/patient-records"; // Redirect to patient records as main page
    }

    /**
     * Display edit record page
     */
    @GetMapping("/edit-record/{id}")
    public String showEditRecord() {
        return "doctor/edit-record";
    }

    /**
     * Display doctor's schedule
     */
    @GetMapping("/schedule")
    public String showDoctorSchedule() {
        return "doctor/schedule";
    }
}