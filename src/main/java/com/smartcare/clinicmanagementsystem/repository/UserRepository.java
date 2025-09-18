package com.smartcare.clinicmanagementsystem.repository;

import com.smartcare.clinicmanagementsystem.model.User;
import com.smartcare.clinicmanagementsystem.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * User Repository
 * Data access layer for User entity
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    /**
     * Find user by email
     */
    Optional<User> findByEmail(String email);
    
    /**
     * Find user by username
     */
    Optional<User> findByUsername(String username);
    
    /**
     * Find users by role
     */
    List<User> findByRole(Role role);
    
    /**
     * Find active users
     */
    List<User> findByActive(Boolean active);
    
    /**
     * Find users by role and active status
     */
    List<User> findByRoleAndActive(Role role, Boolean active);
    
    /**
     * Check if email exists
     */
    boolean existsByEmail(String email);
    
    /**
     * Check if username exists
     */
    boolean existsByUsername(String username);
    
    /**
     * Find users by first name or last name containing (case insensitive)
     */
    @Query("SELECT u FROM User u WHERE " +
           "LOWER(u.firstName) LIKE LOWER(CONCAT('%', :name, '%')) OR " +
           "LOWER(u.lastName) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<User> findByNameContaining(@Param("name") String name);
    
    /**
     * Count users by role
     */
    @Query("SELECT COUNT(u) FROM User u WHERE u.role = :role")
    Long countByRole(@Param("role") Role role);
    
    /**
     * Find all doctors with their specializations
     */
    @Query("SELECT u FROM User u " +
           "LEFT JOIN FETCH u.doctor d " +
           "WHERE u.role = :role AND u.active = true")
    List<User> findActiveDoctorsWithDetails(@Param("role") Role role);
    
    /**
     * Find all patients with their basic info
     */
    @Query("SELECT u FROM User u " +
           "LEFT JOIN FETCH u.patient p " +
           "WHERE u.role = :role AND u.active = true")
    List<User> findActivePatientsWithDetails(@Param("role") Role role);
}