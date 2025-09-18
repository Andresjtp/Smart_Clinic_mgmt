package com.smartcare.clinicmanagementsystem.repository;

import com.smartcare.clinicmanagementsystem.model.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {
    
    // Find doctor by user ID
    Optional<Doctor> findByUserId(Long userId);
    
    // Find doctor by email
    @Query("SELECT d FROM Doctor d JOIN d.user u WHERE u.email = :email")
    Optional<Doctor> findByEmail(@Param("email") String email);
    
    // Find doctors by specialization
    List<Doctor> findBySpecialization(String specialization);
    
    // Find doctors by department
    List<Doctor> findByDepartment(String department);
    
    // Find available doctors (assuming there's an isAvailable field)
    List<Doctor> findByIsAvailableTrue();
    
    // Find doctors by name (first or last name)
    @Query("SELECT d FROM Doctor d JOIN d.user u WHERE " +
           "LOWER(u.firstName) LIKE LOWER(CONCAT('%', :name, '%')) OR " +
           "LOWER(u.lastName) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Doctor> findByName(@Param("name") String name);
    
    // Get all doctors with their user information
    @Query("SELECT d FROM Doctor d JOIN FETCH d.user")
    List<Doctor> findAllWithUser();
    
    // Count doctors by specialization
    @Query("SELECT COUNT(d) FROM Doctor d WHERE d.specialization = :specialization")
    long countBySpecialization(@Param("specialization") String specialization);
    
    // Find doctors by hospital/clinic
    List<Doctor> findByHospitalName(String hospitalName);
}