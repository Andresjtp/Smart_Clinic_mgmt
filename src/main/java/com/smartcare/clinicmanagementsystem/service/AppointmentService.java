package com.smartcare.clinicmanagementsystem.service;

import com.smartcare.clinicmanagementsystem.model.Appointment;
import com.smartcare.clinicmanagementsystem.model.AppointmentStatus;
import com.smartcare.clinicmanagementsystem.model.AppointmentType;
import com.smartcare.clinicmanagementsystem.model.Doctor;
import com.smartcare.clinicmanagementsystem.model.Patient;
import com.smartcare.clinicmanagementsystem.repository.AppointmentRepository;
import com.smartcare.clinicmanagementsystem.repository.DoctorRepository;
import com.smartcare.clinicmanagementsystem.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class AppointmentService {

    @Autowired
    private AppointmentRepository appointmentRepository;
    
    @Autowired
    private DoctorRepository doctorRepository;
    
    @Autowired
    private PatientRepository patientRepository;

    // Create new appointment
    public Appointment createAppointment(Long doctorId, Long patientId, 
                                       LocalDateTime appointmentDateTime, 
                                       Integer durationMinutes,
                                       AppointmentType appointmentType,
                                       String reasonForVisit) {
        
        // Validate doctor exists
        Doctor doctor = doctorRepository.findById(doctorId)
            .orElseThrow(() -> new RuntimeException("Doctor not found with id: " + doctorId));
        
        // Validate patient exists
        Patient patient = patientRepository.findById(patientId)
            .orElseThrow(() -> new RuntimeException("Patient not found with id: " + patientId));
        
        // Check if slot is available
        if (!isSlotAvailable(doctor, appointmentDateTime, durationMinutes)) {
            throw new RuntimeException("The selected time slot is not available");
        }
        
        // Create appointment
        Appointment appointment = new Appointment(doctor, patient, appointmentDateTime, 
                                                durationMinutes, appointmentType);
        appointment.setReasonForVisit(reasonForVisit);
        
        return appointmentRepository.save(appointment);
    }

    // Get appointment by ID
    public Optional<Appointment> getAppointmentById(Long id) {
        return appointmentRepository.findById(id);
    }

    // Get all appointments
    public List<Appointment> getAllAppointments() {
        return appointmentRepository.findAll();
    }

    // Get appointments by doctor
    public List<Appointment> getAppointmentsByDoctor(Long doctorId) {
        Doctor doctor = doctorRepository.findById(doctorId)
            .orElseThrow(() -> new RuntimeException("Doctor not found with id: " + doctorId));
        return appointmentRepository.findByDoctor(doctor);
    }

    // Get appointments by patient
    public List<Appointment> getAppointmentsByPatient(Long patientId) {
        Patient patient = patientRepository.findById(patientId)
            .orElseThrow(() -> new RuntimeException("Patient not found with id: " + patientId));
        return appointmentRepository.findByPatient(patient);
    }

    // Get appointments by status
    public List<Appointment> getAppointmentsByStatus(AppointmentStatus status) {
        return appointmentRepository.findByStatus(status);
    }

    // Get appointments by date range
    public List<Appointment> getAppointmentsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return appointmentRepository.findByDateRange(startDate, endDate);
    }

    // Get doctor's appointments for a specific date
    public List<Appointment> getDoctorAppointmentsForDate(Long doctorId, LocalDate date) {
        Doctor doctor = doctorRepository.findById(doctorId)
            .orElseThrow(() -> new RuntimeException("Doctor not found with id: " + doctorId));
        LocalDateTime startOfDay = date.atStartOfDay();
        return appointmentRepository.findByDoctorAndDate(doctor, startOfDay);
    }

    // Update appointment status
    public Appointment updateAppointmentStatus(Long appointmentId, AppointmentStatus status) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
            .orElseThrow(() -> new RuntimeException("Appointment not found with id: " + appointmentId));
        
        appointment.setStatus(status);
        return appointmentRepository.save(appointment);
    }

    // Cancel appointment
    public Appointment cancelAppointment(Long appointmentId, String reason) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
            .orElseThrow(() -> new RuntimeException("Appointment not found with id: " + appointmentId));
        
        appointment.setStatus(AppointmentStatus.CANCELLED);
        if (reason != null && !reason.trim().isEmpty()) {
            appointment.setNotes(appointment.getNotes() == null ? 
                "Cancellation reason: " + reason : 
                appointment.getNotes() + "\nCancellation reason: " + reason);
        }
        
        return appointmentRepository.save(appointment);
    }

    // Reschedule appointment
    public Appointment rescheduleAppointment(Long appointmentId, 
                                           LocalDateTime newDateTime, 
                                           Integer newDurationMinutes) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
            .orElseThrow(() -> new RuntimeException("Appointment not found with id: " + appointmentId));
        
        // Check if new slot is available
        if (!isSlotAvailable(appointment.getDoctor(), newDateTime, 
                           newDurationMinutes != null ? newDurationMinutes : appointment.getDurationMinutes())) {
            throw new RuntimeException("The selected time slot is not available");
        }
        
        appointment.setAppointmentDateTime(newDateTime);
        if (newDurationMinutes != null) {
            appointment.setDurationMinutes(newDurationMinutes);
        }
        appointment.setStatus(AppointmentStatus.RESCHEDULED);
        
        return appointmentRepository.save(appointment);
    }

    // Check if time slot is available for doctor
    public boolean isSlotAvailable(Doctor doctor, LocalDateTime startTime, Integer durationMinutes) {
        LocalDateTime endTime = startTime.plusMinutes(durationMinutes);
        
        // Get potential conflicting appointments
        List<Appointment> conflictingAppointments = appointmentRepository
            .findPotentialConflictingAppointments(doctor, 
                                                startTime.minusMinutes(480), // Check 8 hours before
                                                endTime.plusMinutes(480));   // Check 8 hours after
        
        // Check for actual conflicts by calculating end times
        for (Appointment existing : conflictingAppointments) {
            LocalDateTime existingEnd = existing.getAppointmentDateTime().plusMinutes(existing.getDurationMinutes());
            
            // Check if appointments overlap
            if (!(endTime.isBefore(existing.getAppointmentDateTime()) || 
                  startTime.isAfter(existingEnd))) {
                return false; // Conflict found
            }
        }
        
        return true; // No conflicts
    }

    // Get upcoming appointments for doctor
    public List<Appointment> getUpcomingAppointmentsForDoctor(Long doctorId) {
        Doctor doctor = doctorRepository.findById(doctorId)
            .orElseThrow(() -> new RuntimeException("Doctor not found with id: " + doctorId));
        
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime weekFromNow = now.plusDays(7);
        
        return appointmentRepository.findUpcomingAppointmentsForDoctor(doctor, now, weekFromNow);
    }

    // Get upcoming appointments for patient
    public List<Appointment> getUpcomingAppointmentsForPatient(Long patientId) {
        Patient patient = patientRepository.findById(patientId)
            .orElseThrow(() -> new RuntimeException("Patient not found with id: " + patientId));
        
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime monthFromNow = now.plusDays(30);
        
        return appointmentRepository.findUpcomingAppointmentsForPatient(patient, now, monthFromNow);
    }

    // Get appointment statistics
    public long getAppointmentCountByStatus(AppointmentStatus status) {
        return appointmentRepository.countByStatus(status);
    }

    // Get today's appointment count
    public long getTodayAppointmentCount() {
        return appointmentRepository.countAppointmentsForToday(LocalDateTime.now());
    }

    // Get completed appointments count
    public long getCompletedAppointmentsCount() {
        return appointmentRepository.countCompletedAppointments();
    }

    // Get appointments requiring follow-up
    public List<Appointment> getAppointmentsRequiringFollowUp() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime weekAgo = now.minusDays(7);
        
        return appointmentRepository.findAppointmentsRequiringFollowUp(weekAgo, now);
    }

    // Update appointment notes
    public Appointment updateAppointmentNotes(Long appointmentId, String notes, String doctorNotes) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
            .orElseThrow(() -> new RuntimeException("Appointment not found with id: " + appointmentId));
        
        if (notes != null) {
            appointment.setNotes(notes);
        }
        if (doctorNotes != null) {
            appointment.setDoctorNotes(doctorNotes);
        }
        
        return appointmentRepository.save(appointment);
    }

    // Mark appointment as completed
    public Appointment completeAppointment(Long appointmentId, String doctorNotes) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
            .orElseThrow(() -> new RuntimeException("Appointment not found with id: " + appointmentId));
        
        appointment.setStatus(AppointmentStatus.COMPLETED);
        if (doctorNotes != null && !doctorNotes.trim().isEmpty()) {
            appointment.setDoctorNotes(doctorNotes);
        }
        
        return appointmentRepository.save(appointment);
    }

    // Update appointment fee and payment status
    public Appointment updateAppointmentPayment(Long appointmentId, Double fee, Boolean isPaid) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
            .orElseThrow(() -> new RuntimeException("Appointment not found with id: " + appointmentId));
        
        if (fee != null) {
            appointment.setFee(fee);
        }
        if (isPaid != null) {
            appointment.setIsPaid(isPaid);
        }
        
        return appointmentRepository.save(appointment);
    }
}