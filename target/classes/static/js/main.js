// Smart Clinic Management System - Main JavaScript Utilities
// This file contains common utilities for API calls, form handling, authentication, and UI interactions

// ==================== CONFIGURATION ====================
const API_BASE_URL = '/api';
const AUTH_TOKEN_KEY = 'authToken';
const USER_DATA_KEY = 'user';

// ==================== AUTHENTICATION UTILITIES ====================
class AuthManager {
    static getAuthToken() {
        return localStorage.getItem(AUTH_TOKEN_KEY);
    }

    static setAuthToken(token) {
        localStorage.setItem(AUTH_TOKEN_KEY, token);
    }

    static removeAuthToken() {
        localStorage.removeItem(AUTH_TOKEN_KEY);
        localStorage.removeItem(USER_DATA_KEY);
    }

    static getCurrentUser() {
        try {
            return JSON.parse(localStorage.getItem(USER_DATA_KEY) || '{}');
        } catch (error) {
            console.error('Error parsing user data:', error);
            return {};
        }
    }

    static setCurrentUser(userData) {
        localStorage.setItem(USER_DATA_KEY, JSON.stringify(userData));
    }

    static isLoggedIn() {
        return !!this.getAuthToken() && !!this.getCurrentUser().id;
    }

    static hasRole(requiredRole) {
        const user = this.getCurrentUser();
        return user.role === requiredRole;
    }

    static logout() {
        this.removeAuthToken();
        window.location.href = '/login';
    }

    static redirectBasedOnRole() {
        const user = this.getCurrentUser();
        if (!user.role) return;

        switch(user.role) {
            case 'ADMIN':
                window.location.href = '/admin/dashboard';
                break;
            case 'DOCTOR':
                window.location.href = '/doctor/patient-records';
                break;
            case 'PATIENT':
                window.location.href = '/patient/doctors';
                break;
            default:
                window.location.href = '/';
        }
    }
}

// ==================== API CLIENT ====================
class ApiClient {
    static async request(endpoint, options = {}) {
        const url = `${API_BASE_URL}${endpoint}`;
        const defaultOptions = {
            headers: {
                'Content-Type': 'application/json',
                ...(AuthManager.getAuthToken() && {
                    'Authorization': `Bearer ${AuthManager.getAuthToken()}`
                })
            }
        };

        const config = {
            ...defaultOptions,
            ...options,
            headers: {
                ...defaultOptions.headers,
                ...options.headers
            }
        };

        try {
            const response = await fetch(url, config);
            
            // Handle authentication errors
            if (response.status === 401) {
                AuthManager.logout();
                return;
            }

            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }

            const data = await response.json();
            return data;
        } catch (error) {
            console.error('API request failed:', error);
            throw error;
        }
    }

    static async get(endpoint) {
        return this.request(endpoint, { method: 'GET' });
    }

    static async post(endpoint, data) {
        return this.request(endpoint, {
            method: 'POST',
            body: JSON.stringify(data)
        });
    }

    static async put(endpoint, data) {
        return this.request(endpoint, {
            method: 'PUT',
            body: JSON.stringify(data)
        });
    }

    static async delete(endpoint) {
        return this.request(endpoint, { method: 'DELETE' });
    }
}

// ==================== FORM VALIDATION ====================
class FormValidator {
    static validateEmail(email) {
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        return emailRegex.test(email);
    }

    static validatePhone(phone) {
        const phoneRegex = /^[\+]?[1-9][\d]{0,15}$/;
        return phoneRegex.test(phone.replace(/[\s\-\(\)]/g, ''));
    }

    static validateRequired(value) {
        return value && value.toString().trim().length > 0;
    }

    static validateMinLength(value, minLength) {
        return value && value.length >= minLength;
    }

    static validateMaxLength(value, maxLength) {
        return !value || value.length <= maxLength;
    }

    static validateDate(dateString) {
        const date = new Date(dateString);
        return date instanceof Date && !isNaN(date);
    }

    static validateFutureDate(dateString) {
        const date = new Date(dateString);
        const today = new Date();
        today.setHours(0, 0, 0, 0);
        return date > today;
    }

    static showFieldError(fieldId, message) {
        const errorElement = document.getElementById(`${fieldId}Error`);
        if (errorElement) {
            errorElement.textContent = message;
            errorElement.style.display = message ? 'block' : 'none';
        }
        
        const field = document.getElementById(fieldId);
        if (field) {
            field.classList.toggle('error', !!message);
        }
    }

    static clearFieldError(fieldId) {
        this.showFieldError(fieldId, '');
    }

    static clearAllErrors(formId) {
        const form = document.getElementById(formId);
        if (form) {
            const errorElements = form.querySelectorAll('.error-message');
            errorElements.forEach(el => {
                el.textContent = '';
                el.style.display = 'none';
            });
            
            const fields = form.querySelectorAll('.form-input, .form-select, .form-textarea');
            fields.forEach(field => field.classList.remove('error'));
        }
    }
}

// ==================== UI UTILITIES ====================
class UIUtils {
    static showNotification(message, type = 'info', duration = 4000) {
        // Remove existing notifications
        const existingNotifications = document.querySelectorAll('.notification');
        existingNotifications.forEach(n => n.remove());

        const notification = document.createElement('div');
        notification.className = `notification ${type}`;
        
        const colors = {
            success: '#2ECC71',
            error: '#E74C3C',
            warning: '#F39C12',
            info: '#3498DB'
        };

        notification.style.cssText = `
            position: fixed;
            top: 20px;
            right: 20px;
            background: ${colors[type] || colors.info};
            color: white;
            padding: 1rem 2rem;
            border-radius: 10px;
            box-shadow: 0 4px 12px rgba(0,0,0,0.2);
            z-index: 10000;
            animation: slideInRight 0.3s ease-out;
            max-width: 400px;
            word-wrap: break-word;
        `;

        notification.innerHTML = `
            <div style="display: flex; align-items: center; gap: 0.5rem;">
                <span>${message}</span>
                <button onclick="this.parentElement.parentElement.remove()" 
                        style="background: none; border: none; color: white; font-size: 1.2rem; cursor: pointer; margin-left: auto;">×</button>
            </div>
        `;

        document.body.appendChild(notification);

        setTimeout(() => {
            if (notification.parentElement) {
                notification.remove();
            }
        }, duration);
    }

    static showModal(modalId) {
        const modal = document.getElementById(modalId);
        if (modal) {
            modal.style.display = 'flex';
            document.body.style.overflow = 'hidden';
        }
    }

    static hideModal(modalId) {
        const modal = document.getElementById(modalId);
        if (modal) {
            modal.style.display = 'none';
            document.body.style.overflow = 'auto';
        }
    }

    static showLoader(elementId) {
        const element = document.getElementById(elementId);
        if (element) {
            const loader = document.createElement('div');
            loader.className = 'loader';
            loader.style.cssText = `
                display: inline-block;
                width: 20px;
                height: 20px;
                border: 2px solid #f3f3f3;
                border-top: 2px solid var(--primary-teal);
                border-radius: 50%;
                animation: spin 1s linear infinite;
            `;
            element.appendChild(loader);
        }
    }

    static hideLoader(elementId) {
        const element = document.getElementById(elementId);
        if (element) {
            const loader = element.querySelector('.loader');
            if (loader) {
                loader.remove();
            }
        }
    }

    static formatDate(dateString, options = {}) {
        const date = new Date(dateString);
        const defaultOptions = {
            year: 'numeric',
            month: 'short',
            day: 'numeric'
        };
        return date.toLocaleDateString('en-US', {...defaultOptions, ...options});
    }

    static formatTime(timeString) {
        const time = new Date(`2000-01-01T${timeString}`);
        return time.toLocaleTimeString('en-US', {
            hour: 'numeric',
            minute: '2-digit',
            hour12: true
        });
    }

    static formatCurrency(amount) {
        return new Intl.NumberFormat('en-US', {
            style: 'currency',
            currency: 'USD'
        }).format(amount);
    }

    static debounce(func, wait) {
        let timeout;
        return function executedFunction(...args) {
            const later = () => {
                clearTimeout(timeout);
                func(...args);
            };
            clearTimeout(timeout);
            timeout = setTimeout(later, wait);
        };
    }

    static animateNumber(element, start, end, duration = 2000) {
        const startTimestamp = performance.now();
        
        function step(timestamp) {
            const elapsed = timestamp - startTimestamp;
            const progress = Math.min(elapsed / duration, 1);
            const current = Math.floor(progress * (end - start) + start);
            
            element.textContent = current.toLocaleString();
            
            if (progress < 1) {
                requestAnimationFrame(step);
            }
        }
        
        requestAnimationFrame(step);
    }
}

// ==================== DATA UTILITIES ====================
class DataUtils {
    static generateTimeSlots(startHour = 9, endHour = 17, intervalMinutes = 30) {
        const slots = [];
        const startTime = new Date();
        startTime.setHours(startHour, 0, 0, 0);
        
        const endTime = new Date();
        endTime.setHours(endHour, 0, 0, 0);
        
        const current = new Date(startTime);
        
        while (current < endTime) {
            slots.push(current.toTimeString().slice(0, 5));
            current.setMinutes(current.getMinutes() + intervalMinutes);
        }
        
        return slots;
    }

    static filterArray(array, searchTerm, searchFields) {
        if (!searchTerm) return array;
        
        const term = searchTerm.toLowerCase();
        return array.filter(item => {
            return searchFields.some(field => {
                const value = this.getNestedValue(item, field);
                return value && value.toString().toLowerCase().includes(term);
            });
        });
    }

    static sortArray(array, field, direction = 'asc') {
        return array.sort((a, b) => {
            const aVal = this.getNestedValue(a, field);
            const bVal = this.getNestedValue(b, field);
            
            if (aVal < bVal) return direction === 'asc' ? -1 : 1;
            if (aVal > bVal) return direction === 'asc' ? 1 : -1;
            return 0;
        });
    }

    static getNestedValue(obj, path) {
        return path.split('.').reduce((current, key) => current && current[key], obj);
    }

    static groupBy(array, key) {
        return array.reduce((groups, item) => {
            const value = this.getNestedValue(item, key);
            (groups[value] = groups[value] || []).push(item);
            return groups;
        }, {});
    }
}

// ==================== FORM HANDLERS ====================
class FormHandler {
    static async handleFormSubmission(formId, submitFunction, options = {}) {
        const form = document.getElementById(formId);
        if (!form) return;

        const formData = new FormData(form);
        const data = Object.fromEntries(formData.entries());
        
        // Clear previous errors
        FormValidator.clearAllErrors(formId);

        // Show loading state
        const submitBtn = form.querySelector('button[type="submit"]');
        const originalText = submitBtn.textContent;
        
        if (options.showLoading !== false) {
            submitBtn.disabled = true;
            submitBtn.innerHTML = '<div class="btn-loader"></div> Processing...';
        }

        try {
            await submitFunction(data);
            
            if (options.successMessage) {
                UIUtils.showNotification(options.successMessage, 'success');
            }
            
            if (options.resetForm !== false) {
                form.reset();
            }
            
            if (options.redirectTo) {
                setTimeout(() => {
                    window.location.href = options.redirectTo;
                }, 1500);
            }
            
        } catch (error) {
            console.error('Form submission error:', error);
            
            if (error.validationErrors) {
                // Handle field-specific validation errors
                Object.entries(error.validationErrors).forEach(([field, message]) => {
                    FormValidator.showFieldError(field, message);
                });
            } else {
                UIUtils.showNotification(
                    error.message || 'An error occurred. Please try again.',
                    'error'
                );
            }
        } finally {
            // Restore button state
            if (options.showLoading !== false) {
                submitBtn.disabled = false;
                submitBtn.textContent = originalText;
            }
        }
    }

    static setupRealTimeValidation(fieldId, validator, errorMessage) {
        const field = document.getElementById(fieldId);
        if (!field) return;

        const validateField = () => {
            const isValid = validator(field.value);
            if (field.value && !isValid) {
                FormValidator.showFieldError(fieldId, errorMessage);
            } else {
                FormValidator.clearFieldError(fieldId);
            }
        };

        field.addEventListener('blur', validateField);
        field.addEventListener('input', UIUtils.debounce(validateField, 300));
    }
}

// ==================== INITIALIZATION ====================
document.addEventListener('DOMContentLoaded', () => {
    // Add global styles for animations and utilities
    const styles = `
        <style>
            @keyframes slideInRight {
                from { transform: translateX(100%); opacity: 0; }
                to { transform: translateX(0); opacity: 1; }
            }
            
            @keyframes spin {
                0% { transform: rotate(0deg); }
                100% { transform: rotate(360deg); }
            }
            
            .btn-loader {
                display: inline-block;
                width: 16px;
                height: 16px;
                border: 2px solid rgba(255,255,255,0.3);
                border-top: 2px solid white;
                border-radius: 50%;
                animation: spin 1s linear infinite;
                margin-right: 0.5rem;
            }
            
            .form-input.error,
            .form-select.error,
            .form-textarea.error {
                border-color: var(--error-red) !important;
                box-shadow: 0 0 0 2px rgba(231, 76, 60, 0.1) !important;
            }
            
            .error-message {
                display: none;
                color: var(--error-red);
                font-size: 0.8rem;
                margin-top: 0.5rem;
            }
        </style>
    `;
    document.head.insertAdjacentHTML('beforeend', styles);

    // Check authentication status on protected pages
    const protectedPaths = ['/admin/', '/doctor/', '/patient/'];
    const currentPath = window.location.pathname;
    
    if (protectedPaths.some(path => currentPath.includes(path))) {
        if (!AuthManager.isLoggedIn()) {
            window.location.href = '/login';
            return;
        }
        
        // Check role-based access
        const user = AuthManager.getCurrentUser();
        const requiredRole = currentPath.includes('/admin/') ? 'ADMIN' :
                           currentPath.includes('/doctor/') ? 'DOCTOR' : 
                           currentPath.includes('/patient/') ? 'PATIENT' : null;
        
        if (requiredRole && !AuthManager.hasRole(requiredRole)) {
            UIUtils.showNotification('Access denied. Insufficient permissions.', 'error');
            setTimeout(() => {
                AuthManager.redirectBasedOnRole();
            }, 2000);
        }
    }

    // Setup logout functionality
    const logoutLinks = document.querySelectorAll('a[href="/logout"]');
    logoutLinks.forEach(link => {
        link.addEventListener('click', (e) => {
            e.preventDefault();
            if (confirm('Are you sure you want to logout?')) {
                AuthManager.logout();
            }
        });
    });

    // Setup modal close functionality
    window.addEventListener('click', (e) => {
        if (e.target.classList.contains('modal')) {
            e.target.style.display = 'none';
            document.body.style.overflow = 'auto';
        }
    });

    // Add escape key support for modals
    document.addEventListener('keydown', (e) => {
        if (e.key === 'Escape') {
            const visibleModals = document.querySelectorAll('.modal[style*="flex"]');
            visibleModals.forEach(modal => {
                modal.style.display = 'none';
                document.body.style.overflow = 'auto';
            });
        }
    });
});

// ==================== GLOBAL EXPORTS ====================
// Make utilities available globally
window.AuthManager = AuthManager;
window.ApiClient = ApiClient;
window.FormValidator = FormValidator;
window.UIUtils = UIUtils;
window.DataUtils = DataUtils;
window.FormHandler = FormHandler;