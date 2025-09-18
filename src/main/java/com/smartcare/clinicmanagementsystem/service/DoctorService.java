package com.smartcare.clinicmanagementsystem.service;

import com.smartcare.clinicmanagementsystem.model.Doctor;
import com.smartcare.clinicmanagementsystem.model.User;
import com.smartcare.clinicmanagementsystem.model.Role;
import com.smartcare.clinicmanagementsystem.repository.DoctorRepository;
import com.smartcare.clinicmanagementsystem.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Doctor Service
 * Handles business logic for doctor-related operations
 */
@Service
@Transactional
public class DoctorService {

    @Autowired
    private DoctorRepository doctorRepository;
    
    @Autowired
    private UserRepository userRepository;

    /**
     * Create new doctor profile
     */
    public Doctor createDoctor(Long userId, String specialization, String licenseNumber, 
                              String department, String hospitalName, String qualifications) {
        
        // Validate user exists and has DOCTOR role
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        
        if (user.getRole() != Role.DOCTOR) {
            throw new RuntimeException("User must have DOCTOR role to create doctor profile");
        }
        
        // Check if doctor profile already exists for this user
        Optional<Doctor> existingDoctor = doctorRepository.findByUserId(userId);
        if (existingDoctor.isPresent()) {
            throw new RuntimeException("Doctor profile already exists for user id: " + userId);
        }
        
        // Create new doctor
        Doctor doctor = new Doctor();
        doctor.setUser(user);
        doctor.setSpecialization(specialization);
        doctor.setLicenseNumber(licenseNumber);
        doctor.setDepartment(department);
        doctor.setHospitalName(hospitalName);
        doctor.setQualifications(qualifications);
        doctor.setIsAvailable(true);
        doctor.setCreatedAt(LocalDateTime.now());
        
        return doctorRepository.save(doctor);
    }

    /**
     * Get doctor by ID
     */
    public Optional<Doctor> getDoctorById(Long id) {
        return doctorRepository.findById(id);
    }

    /**
     * Get doctor by user ID
     */
    public Optional<Doctor> getDoctorByUserId(Long userId) {
        return doctorRepository.findByUserId(userId);
    }

    /**
     * Get doctor by email
     */
    public Optional<Doctor> getDoctorByEmail(String email) {
        return doctorRepository.findByEmail(email);
    }

    /**
     * Get all doctors
     */
    public List<Doctor> getAllDoctors() {
        return doctorRepository.findAll();
    }

    /**
     * Get all doctors with user information
     */
    public List<Doctor> getAllDoctorsWithUser() {
        return doctorRepository.findAllWithUser();
    }

    /**
     * Get available doctors
     */
    public List<Doctor> getAvailableDoctors() {
        return doctorRepository.findByIsAvailableTrue();
    }

    /**
     * Get doctors by specialization
     */
    public List<Doctor> getDoctorsBySpecialization(String specialization) {
        return doctorRepository.findBySpecialization(specialization);
    }

    /**
     * Get doctors by department
     */
    public List<Doctor> getDoctorsByDepartment(String department) {
        return doctorRepository.findByDepartment(department);
    }

    /**
     * Get doctors by hospital
     */
    public List<Doctor> getDoctorsByHospital(String hospitalName) {
        return doctorRepository.findByHospitalName(hospitalName);
    }

    /**
     * Search doctors by name
     */
    public List<Doctor> searchDoctorsByName(String name) {
        return doctorRepository.findByName(name);
    }

    /**
     * Update doctor profile
     */
    public Doctor updateDoctor(Long doctorId, String specialization, String licenseNumber,
                              String department, String hospitalName, String qualifications,
                              Boolean isAvailable) {
        Doctor doctor = doctorRepository.findById(doctorId)
            .orElseThrow(() -> new RuntimeException("Doctor not found with id: " + doctorId));
        
        if (specialization != null && !specialization.trim().isEmpty()) {
            doctor.setSpecialization(specialization);
        }
        if (licenseNumber != null && !licenseNumber.trim().isEmpty()) {
            doctor.setLicenseNumber(licenseNumber);
        }
        if (department != null && !department.trim().isEmpty()) {
            doctor.setDepartment(department);
        }
        if (hospitalName != null && !hospitalName.trim().isEmpty()) {
            doctor.setHospitalName(hospitalName);
        }
        if (qualifications != null && !qualifications.trim().isEmpty()) {
            doctor.setQualifications(qualifications);
        }
        if (isAvailable != null) {
            doctor.setIsAvailable(isAvailable);
        }
        
        doctor.setUpdatedAt(LocalDateTime.now());
        
        return doctorRepository.save(doctor);
    }

    /**
     * Update doctor availability status
     */
    public Doctor updateDoctorAvailability(Long doctorId, boolean isAvailable) {
        Doctor doctor = doctorRepository.findById(doctorId)
            .orElseThrow(() -> new RuntimeException("Doctor not found with id: " + doctorId));
        
        doctor.setIsAvailable(isAvailable);
        doctor.setUpdatedAt(LocalDateTime.now());
        
        return doctorRepository.save(doctor);
    }

    /**
     * Set doctor as unavailable
     */
    public Doctor setDoctorUnavailable(Long doctorId, String reason) {
        Doctor doctor = doctorRepository.findById(doctorId)
            .orElseThrow(() -> new RuntimeException("Doctor not found with id: " + doctorId));
        
        doctor.setIsAvailable(false);
        doctor.setUnavailabilityReason(reason);
        doctor.setUpdatedAt(LocalDateTime.now());
        
        return doctorRepository.save(doctor);
    }

    /**
     * Set doctor as available
     */
    public Doctor setDoctorAvailable(Long doctorId) {
        Doctor doctor = doctorRepository.findById(doctorId)
            .orElseThrow(() -> new RuntimeException("Doctor not found with id: " + doctorId));
        
        doctor.setIsAvailable(true);
        doctor.setUnavailabilityReason(null);
        doctor.setUpdatedAt(LocalDateTime.now());
        
        return doctorRepository.save(doctor);
    }

    /**
     * Update doctor specialization
     */
    public Doctor updateDoctorSpecialization(Long doctorId, String specialization) {
        Doctor doctor = doctorRepository.findById(doctorId)
            .orElseThrow(() -> new RuntimeException("Doctor not found with id: " + doctorId));
        
        doctor.setSpecialization(specialization);
        doctor.setUpdatedAt(LocalDateTime.now());
        
        return doctorRepository.save(doctor);
    }

    /**
     * Update doctor department
     */
    public Doctor updateDoctorDepartment(Long doctorId, String department) {
        Doctor doctor = doctorRepository.findById(doctorId)
            .orElseThrow(() -> new RuntimeException("Doctor not found with id: " + doctorId));
        
        doctor.setDepartment(department);
        doctor.setUpdatedAt(LocalDateTime.now());
        
        return doctorRepository.save(doctor);
    }

    /**
     * Add qualification to doctor
     */
    public Doctor addDoctorQualification(Long doctorId, String newQualification) {
        Doctor doctor = doctorRepository.findById(doctorId)
            .orElseThrow(() -> new RuntimeException("Doctor not found with id: " + doctorId));
        
        String currentQualifications = doctor.getQualifications();
        if (currentQualifications == null || currentQualifications.trim().isEmpty()) {
            doctor.setQualifications(newQualification);
        } else {
            doctor.setQualifications(currentQualifications + "; " + newQualification);
        }
        
        doctor.setUpdatedAt(LocalDateTime.now());
        
        return doctorRepository.save(doctor);
    }

    /**
     * Update doctor consultation fee
     */
    public Doctor updateConsultationFee(Long doctorId, Double consultationFee) {
        Doctor doctor = doctorRepository.findById(doctorId)
            .orElseThrow(() -> new RuntimeException("Doctor not found with id: " + doctorId));
        
        doctor.setConsultationFee(consultationFee);
        doctor.setUpdatedAt(LocalDateTime.now());
        
        return doctorRepository.save(doctor);
    }

    /**
     * Update doctor working hours
     */
    public Doctor updateWorkingHours(Long doctorId, String workingHours) {
        Doctor doctor = doctorRepository.findById(doctorId)
            .orElseThrow(() -> new RuntimeException("Doctor not found with id: " + doctorId));
        
        doctor.setWorkingHours(workingHours);
        doctor.setUpdatedAt(LocalDateTime.now());
        
        return doctorRepository.save(doctor);
    }

    /**
     * Get doctor statistics
     */
    public long countDoctorsBySpecialization(String specialization) {
        return doctorRepository.countBySpecialization(specialization);
    }

    /**
     * Get total doctors count
     */
    public long getTotalDoctorsCount() {
        return doctorRepository.count();
    }

    /**
     * Get available doctors count
     */
    public long getAvailableDoctorsCount() {
        return doctorRepository.findByIsAvailableTrue().size();
    }

    /**
     * Delete doctor profile
     */
    public void deleteDoctor(Long doctorId) {
        Doctor doctor = doctorRepository.findById(doctorId)
            .orElseThrow(() -> new RuntimeException("Doctor not found with id: " + doctorId));
        
        doctorRepository.delete(doctor);
    }

    /**
     * Check if doctor exists
     */
    public boolean existsById(Long doctorId) {
        return doctorRepository.existsById(doctorId);
    }

    /**
     * Check if user has doctor profile
     */
    public boolean existsByUserId(Long userId) {
        return doctorRepository.findByUserId(userId).isPresent();
    }

    /**
     * Validate doctor license
     */
    public boolean validateDoctorLicense(String licenseNumber) {
        // This would typically integrate with external license validation service
        // For now, just check format and uniqueness
        if (licenseNumber == null || licenseNumber.trim().isEmpty()) {
            return false;
        }
        
        // Check if license number already exists
        List<Doctor> allDoctors = doctorRepository.findAll();
        return allDoctors.stream()
                .noneMatch(d -> licenseNumber.equals(d.getLicenseNumber()));
    }

    /**
     * Get doctors by multiple criteria
     */
    public List<Doctor> searchDoctors(String specialization, String department, 
                                    String hospitalName, Boolean isAvailable) {
        List<Doctor> doctors = doctorRepository.findAll();
        
        return doctors.stream()
                .filter(d -> specialization == null || specialization.isEmpty() || 
                           d.getSpecialization().toLowerCase().contains(specialization.toLowerCase()))
                .filter(d -> department == null || department.isEmpty() || 
                           d.getDepartment().toLowerCase().contains(department.toLowerCase()))
                .filter(d -> hospitalName == null || hospitalName.isEmpty() || 
                           d.getHospitalName().toLowerCase().contains(hospitalName.toLowerCase()))
                .filter(d -> isAvailable == null || d.getIsAvailable().equals(isAvailable))
                .collect(java.util.stream.Collectors.toList());
    }

    /**
     * Get doctor's full name
     */
    public String getDoctorFullName(Long doctorId) {
        Doctor doctor = doctorRepository.findById(doctorId)
            .orElseThrow(() -> new RuntimeException("Doctor not found with id: " + doctorId));
        
        User user = doctor.getUser();
        if (user != null) {
            return user.getFirstName() + " " + user.getLastName();
        }
        return "Unknown Doctor";
    }

    /**
     * Get doctor's contact information
     */
    public String getDoctorContactInfo(Long doctorId) {
        Doctor doctor = doctorRepository.findById(doctorId)
            .orElseThrow(() -> new RuntimeException("Doctor not found with id: " + doctorId));
        
        User user = doctor.getUser();
        if (user != null) {
            return "Email: " + user.getEmail() + 
                   (user.getPhoneNumber() != null ? ", Phone: " + user.getPhoneNumber() : "");
        }
        return "Contact information not available";
    }

    /**
     * Update doctor profile picture
     */
    public Doctor updateProfilePicture(Long doctorId, String profilePictureUrl) {
        Doctor doctor = doctorRepository.findById(doctorId)
            .orElseThrow(() -> new RuntimeException("Doctor not found with id: " + doctorId));
        
        doctor.setProfilePictureUrl(profilePictureUrl);
        doctor.setUpdatedAt(LocalDateTime.now());
        
        return doctorRepository.save(doctor);
    }
}