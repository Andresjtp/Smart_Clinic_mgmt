package com.smartcare.clinicmanagementsystem.repository;

import com.smartcare.clinicmanagementsystem.model.Appointment;
import com.smartcare.clinicmanagementsystem.model.AppointmentStatus;
import com.smartcare.clinicmanagementsystem.model.Doctor;
import com.smartcare.clinicmanagementsystem.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    
    // Find appointments by doctor
    List<Appointment> findByDoctor(Doctor doctor);
    
    // Find appointments by patient
    List<Appointment> findByPatient(Patient patient);
    
    // Find appointments by status
    List<Appointment> findByStatus(AppointmentStatus status);
    
    // Find appointments by doctor and date range
    @Query("SELECT a FROM Appointment a WHERE a.doctor = :doctor AND a.appointmentDateTime BETWEEN :startDate AND :endDate ORDER BY a.appointmentDateTime")
    List<Appointment> findByDoctorAndDateRange(@Param("doctor") Doctor doctor, 
                                             @Param("startDate") LocalDateTime startDate, 
                                             @Param("endDate") LocalDateTime endDate);
    
    // Find appointments by patient and date range
    @Query("SELECT a FROM Appointment a WHERE a.patient = :patient AND a.appointmentDateTime BETWEEN :startDate AND :endDate ORDER BY a.appointmentDateTime")
    List<Appointment> findByPatientAndDateRange(@Param("patient") Patient patient, 
                                              @Param("startDate") LocalDateTime startDate, 
                                              @Param("endDate") LocalDateTime endDate);
    
    // Find appointments by date range
    @Query("SELECT a FROM Appointment a WHERE a.appointmentDateTime BETWEEN :startDate AND :endDate ORDER BY a.appointmentDateTime")
    List<Appointment> findByDateRange(@Param("startDate") LocalDateTime startDate, 
                                    @Param("endDate") LocalDateTime endDate);
    
    // Find appointments by doctor and status
    List<Appointment> findByDoctorAndStatus(Doctor doctor, AppointmentStatus status);
    
    // Find appointments by patient and status
    List<Appointment> findByPatientAndStatus(Patient patient, AppointmentStatus status);
    
    // Check for appointment conflicts (overlapping appointments for a doctor)
    @Query("SELECT a FROM Appointment a WHERE a.doctor = :doctor AND " +
           "a.status NOT IN ('CANCELLED', 'NO_SHOW') AND " +
           "a.appointmentDateTime BETWEEN :startTime AND :endTime")
    List<Appointment> findPotentialConflictingAppointments(@Param("doctor") Doctor doctor,
                                                          @Param("startTime") LocalDateTime startTime,
                                                          @Param("endTime") LocalDateTime endTime);
    
    // Find upcoming appointments for a doctor (within next 7 days)
    @Query("SELECT a FROM Appointment a WHERE a.doctor = :doctor AND " +
           "a.appointmentDateTime >= :now AND a.appointmentDateTime <= :weekFromNow AND " +
           "a.status NOT IN ('CANCELLED', 'NO_SHOW') " +
           "ORDER BY a.appointmentDateTime")
    List<Appointment> findUpcomingAppointmentsForDoctor(@Param("doctor") Doctor doctor,
                                                       @Param("now") LocalDateTime now,
                                                       @Param("weekFromNow") LocalDateTime weekFromNow);
    
    // Find upcoming appointments for a patient (within next 30 days)
    @Query("SELECT a FROM Appointment a WHERE a.patient = :patient AND " +
           "a.appointmentDateTime >= :now AND a.appointmentDateTime <= :monthFromNow AND " +
           "a.status NOT IN ('CANCELLED', 'NO_SHOW') " +
           "ORDER BY a.appointmentDateTime")
    List<Appointment> findUpcomingAppointmentsForPatient(@Param("patient") Patient patient,
                                                        @Param("now") LocalDateTime now,
                                                        @Param("monthFromNow") LocalDateTime monthFromNow);
    
    // Find appointments by doctor for a specific date
    @Query("SELECT a FROM Appointment a WHERE a.doctor = :doctor AND " +
           "DATE(a.appointmentDateTime) = DATE(:date) " +
           "ORDER BY a.appointmentDateTime")
    List<Appointment> findByDoctorAndDate(@Param("doctor") Doctor doctor, @Param("date") LocalDateTime date);
    
    // Get appointment statistics for dashboard
    @Query("SELECT COUNT(a) FROM Appointment a WHERE a.status = :status")
    long countByStatus(@Param("status") AppointmentStatus status);
    
    // Count appointments for today
    @Query("SELECT COUNT(a) FROM Appointment a WHERE " +
           "DATE(a.appointmentDateTime) = DATE(:today)")
    long countAppointmentsForToday(@Param("today") LocalDateTime today);
    
    // Count completed appointments
    @Query("SELECT COUNT(a) FROM Appointment a WHERE a.status = 'COMPLETED'")
    long countCompletedAppointments();
    
    // Find appointments requiring follow-up (completed appointments from last week)
    @Query("SELECT a FROM Appointment a WHERE a.status = 'COMPLETED' AND " +
           "a.appointmentDateTime BETWEEN :weekAgo AND :now " +
           "ORDER BY a.appointmentDateTime DESC")
    List<Appointment> findAppointmentsRequiringFollowUp(@Param("weekAgo") LocalDateTime weekAgo,
                                                        @Param("now") LocalDateTime now);
}