package com.smartcare.clinicmanagementsystem.service;

import com.smartcare.clinicmanagementsystem.model.User;
import com.smartcare.clinicmanagementsystem.model.Role;
import com.smartcare.clinicmanagementsystem.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * User Service
 * Business logic for user management operations
 */
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    /**
     * Find user by email
     */
    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    /**
     * Find user by ID
     */
    public User findById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    /**
     * Get all users
     */
    public List<User> findAll() {
        return userRepository.findAll();
    }

    /**
     * Save or update user
     */
    public User save(User user) {
        return userRepository.save(user);
    }

    /**
     * Delete user by ID
     */
    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }

    /**
     * Check if user exists by email
     */
    public boolean existsByEmail(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    /**
     * Find users by role
     */
    public List<User> findByRole(Role role) {
        return userRepository.findByRole(role);
    }

    /**
     * Authenticate user credentials (basic implementation)
     * In a real application, passwords should be properly hashed
     */
    public User authenticate(String email, String password) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            // In production, use proper password hashing (BCrypt)
            // For demo purposes, we're doing plain text comparison
            if (password.equals(user.getPassword())) {
                return user;
            }
        }
        return null;
    }

    /**
     * Update user password
     */
    public boolean updatePassword(Long userId, String newPassword) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            // In production, hash the password before saving
            user.setPassword(newPassword);
            userRepository.save(user);
            return true;
        }
        return false;
    }

    /**
     * Enable or disable user account
     */
    public boolean updateUserStatus(Long userId, boolean enabled) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            // Assuming there's an enabled field (would need to be added to User entity)
            // user.setEnabled(enabled);
            userRepository.save(user);
            return true;
        }
        return false;
    }
}