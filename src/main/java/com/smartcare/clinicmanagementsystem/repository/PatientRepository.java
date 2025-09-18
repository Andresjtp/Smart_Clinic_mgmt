package com.smartcare.clinicmanagementsystem.repository;

import com.smartcare.clinicmanagementsystem.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {
    
    // Find patient by user ID
    Optional<Patient> findByUserId(Long userId);
    
    // Find patient by email
    @Query("SELECT p FROM Patient p JOIN p.user u WHERE u.email = :email")
    Optional<Patient> findByEmail(@Param("email") String email);
    
    // Find patients by name (first or last name)
    @Query("SELECT p FROM Patient p JOIN p.user u WHERE " +
           "LOWER(u.firstName) LIKE LOWER(CONCAT('%', :name, '%')) OR " +
           "LOWER(u.lastName) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Patient> findByName(@Param("name") String name);
    
    // Find patient by phone number
    @Query("SELECT p FROM Patient p JOIN p.user u WHERE u.phoneNumber = :phoneNumber")
    Optional<Patient> findByPhoneNumber(@Param("phoneNumber") String phoneNumber);
    
    // Find patients by date of birth
    @Query("SELECT p FROM Patient p WHERE p.dateOfBirth = :dateOfBirth")
    List<Patient> findByDateOfBirth(@Param("dateOfBirth") LocalDate dateOfBirth);
    
    // Find patients by age range
    @Query("SELECT p FROM Patient p WHERE " +
           "FUNCTION('YEAR', CURRENT_DATE) - FUNCTION('YEAR', p.dateOfBirth) BETWEEN :minAge AND :maxAge")
    List<Patient> findByAgeRange(@Param("minAge") int minAge, @Param("maxAge") int maxAge);
    
    // Find patients by gender
    List<Patient> findByGender(String gender);
    
    // Find patients by blood group
    List<Patient> findByBloodGroup(String bloodGroup);
    
    // Find patients by emergency contact
    List<Patient> findByEmergencyContactName(String emergencyContactName);
    
    // Get all patients with their user information
    @Query("SELECT p FROM Patient p JOIN FETCH p.user")
    List<Patient> findAllWithUser();
    
    // Find patients registered within a date range
    @Query("SELECT p FROM Patient p JOIN p.user u WHERE u.createdAt BETWEEN :startDate AND :endDate")
    List<Patient> findByRegistrationDateRange(@Param("startDate") LocalDate startDate, 
                                            @Param("endDate") LocalDate endDate);
    
    // Count patients by gender
    @Query("SELECT COUNT(p) FROM Patient p WHERE p.gender = :gender")
    long countByGender(@Param("gender") String gender);
    
    // Count patients by blood group
    @Query("SELECT COUNT(p) FROM Patient p WHERE p.bloodGroup = :bloodGroup")
    long countByBloodGroup(@Param("bloodGroup") String bloodGroup);
    
    // Find patients with allergies
    @Query("SELECT p FROM Patient p WHERE p.allergies IS NOT NULL AND p.allergies != ''")
    List<Patient> findPatientsWithAllergies();
    
    // Search patients by multiple criteria
    @Query("SELECT p FROM Patient p JOIN p.user u WHERE " +
           "(:name IS NULL OR LOWER(u.firstName) LIKE LOWER(CONCAT('%', :name, '%')) OR " +
           "LOWER(u.lastName) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
           "(:email IS NULL OR u.email = :email) AND " +
           "(:phoneNumber IS NULL OR u.phoneNumber = :phoneNumber)")
    List<Patient> searchPatients(@Param("name") String name, 
                               @Param("email") String email, 
                               @Param("phoneNumber") String phoneNumber);
}