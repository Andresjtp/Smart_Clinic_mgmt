package com.smartcare.clinicmanagementsystem.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Admin Controller
 * Handles admin dashboard and administrative functions
 */
@Controller
@RequestMapping("/admin")
public class AdminController {

    /**
     * Display admin dashboard
     */
    @GetMapping("/dashboard")
    public String showAdminDashboard() {
        return "admin/dashboard";
    }

    /**
     * Display admin reports page
     */
    @GetMapping("/reports")
    public String showAdminReports() {
        return "admin/reports";
    }

    /**
     * Display user management page
     */
    @GetMapping("/users")
    public String showUserManagement() {
        return "admin/users";
    }

    /**
     * Display add new user page
     */
    @GetMapping("/add-user")
    public String showAddUserPage() {
        return "admin/add-user";
    }

    /**
     * Display appointment management page
     */
    @GetMapping("/appointments")
    public String showAppointmentManagement() {
        return "admin/appointments";
    }

    /**
     * Display system settings page
     */
    @GetMapping("/settings")
    public String showSystemSettings() {
        return "admin/settings";
    }

    /**
     * Display system activity logs
     */
    @GetMapping("/logs")
    public String showActivityLogs() {
        return "admin/logs";
    }
}