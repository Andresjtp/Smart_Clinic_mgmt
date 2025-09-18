-- Stored Procedures for Smart Clinic Management System
-- Common reporting and query operations

DELIMITER //

-- 1. Get Patient Summary Report
-- Returns comprehensive patient information with appointment and payment history
DROP PROCEDURE IF EXISTS GetPatientSummaryReport//
CREATE PROCEDURE GetPatientSummaryReport(IN patientUserId BIGINT)
BEGIN
    SELECT 
        u.id as user_id,
        CONCAT(u.first_name, ' ', u.last_name) as patient_name,
        u.email,
        p.date_of_birth,
        p.gender,
        p.phone_number,
        p.address,
        CONCAT(p.city, ', ', p.state, ' ', p.zip_code) as full_address,
        p.insurance_provider,
        p.insurance_policy_number,
        p.emergency_contact_name,
        p.emergency_contact_phone,
        COUNT(a.id) as total_appointments,
        COUNT(CASE WHEN a.status = 'COMPLETED' THEN 1 END) as completed_appointments,
        COUNT(CASE WHEN a.status = 'CANCELLED' THEN 1 END) as cancelled_appointments,
        COUNT(CASE WHEN a.status = 'NO_SHOW' THEN 1 END) as no_show_appointments,
        SUM(CASE WHEN a.is_paid = true THEN a.fee ELSE 0 END) as total_paid,
        SUM(CASE WHEN a.is_paid = false THEN a.fee ELSE 0 END) as outstanding_balance,
        MAX(a.appointment_datetime) as last_appointment_date
    FROM users u
    JOIN patients p ON u.id = p.user_id
    LEFT JOIN appointments a ON p.id = a.patient_id
    WHERE u.id = patientUserId AND u.role = 'PATIENT'
    GROUP BY u.id, u.first_name, u.last_name, u.email, p.date_of_birth, 
             p.gender, p.phone_number, p.address, p.city, p.state, p.zip_code,
             p.insurance_provider, p.insurance_policy_number, 
             p.emergency_contact_name, p.emergency_contact_phone;
END//

-- 2. Get Doctor Performance Analytics
-- Returns doctor statistics including appointment counts, revenue, and patient satisfaction
DROP PROCEDURE IF EXISTS GetDoctorPerformanceReport//
CREATE PROCEDURE GetDoctorPerformanceReport(IN doctorUserId BIGINT, IN fromDate DATE, IN toDate DATE)
BEGIN
    SELECT 
        u.id as user_id,
        CONCAT(u.first_name, ' ', u.last_name) as doctor_name,
        d.specialization,
        d.license_number,
        d.years_of_experience,
        d.consultation_fee,
        COUNT(a.id) as total_appointments,
        COUNT(CASE WHEN a.status = 'COMPLETED' THEN 1 END) as completed_appointments,
        COUNT(CASE WHEN a.status = 'CANCELLED' THEN 1 END) as cancelled_appointments,
        COUNT(CASE WHEN a.status = 'NO_SHOW' THEN 1 END) as no_show_appointments,
        ROUND((COUNT(CASE WHEN a.status = 'COMPLETED' THEN 1 END) * 100.0) / NULLIF(COUNT(a.id), 0), 2) as completion_rate,
        SUM(CASE WHEN a.is_paid = true THEN a.fee ELSE 0 END) as total_revenue,
        AVG(a.fee) as average_fee,
        COUNT(DISTINCT a.patient_id) as unique_patients,
        MIN(a.appointment_datetime) as first_appointment,
        MAX(a.appointment_datetime) as last_appointment
    FROM users u
    JOIN doctors d ON u.id = d.user_id
    LEFT JOIN appointments a ON d.id = a.doctor_id 
        AND a.appointment_datetime BETWEEN fromDate AND toDate
    WHERE u.id = doctorUserId AND u.role = 'DOCTOR'
    GROUP BY u.id, u.first_name, u.last_name, d.specialization, d.license_number,
             d.years_of_experience, d.consultation_fee;
END//

-- 3. Get Monthly Appointment Analytics
-- Returns appointment statistics for a given month and year
DROP PROCEDURE IF EXISTS GetMonthlyAppointmentAnalytics//
CREATE PROCEDURE GetMonthlyAppointmentAnalytics(IN reportYear INT, IN reportMonth INT)
BEGIN
    SELECT 
        COUNT(*) as total_appointments,
        COUNT(CASE WHEN status = 'SCHEDULED' THEN 1 END) as scheduled_appointments,
        COUNT(CASE WHEN status = 'CONFIRMED' THEN 1 END) as confirmed_appointments,
        COUNT(CASE WHEN status = 'COMPLETED' THEN 1 END) as completed_appointments,
        COUNT(CASE WHEN status = 'CANCELLED' THEN 1 END) as cancelled_appointments,
        COUNT(CASE WHEN status = 'NO_SHOW' THEN 1 END) as no_show_appointments,
        ROUND(AVG(fee), 2) as average_fee,
        SUM(CASE WHEN is_paid = true THEN fee ELSE 0 END) as total_revenue,
        SUM(CASE WHEN is_paid = false THEN fee ELSE 0 END) as pending_payments,
        COUNT(CASE WHEN appointment_type = 'CONSULTATION' THEN 1 END) as consultations,
        COUNT(CASE WHEN appointment_type = 'FOLLOW_UP' THEN 1 END) as follow_ups,
        COUNT(CASE WHEN appointment_type = 'CHECK_UP' THEN 1 END) as checkups,
        COUNT(CASE WHEN appointment_type = 'EMERGENCY' THEN 1 END) as emergencies,
        COUNT(DISTINCT patient_id) as unique_patients,
        COUNT(DISTINCT doctor_id) as active_doctors
    FROM appointments
    WHERE YEAR(appointment_datetime) = reportYear 
      AND MONTH(appointment_datetime) = reportMonth;
END//

-- 4. Get Patient Payment History
-- Returns detailed payment history for a specific patient
DROP PROCEDURE IF EXISTS GetPatientPaymentHistory//
CREATE PROCEDURE GetPatientPaymentHistory(IN patientUserId BIGINT)
BEGIN
    SELECT 
        a.id as appointment_id,
        a.appointment_datetime,
        CONCAT(du.first_name, ' ', du.last_name) as doctor_name,
        ds.specialization as doctor_specialty,
        a.appointment_type,
        a.reason_for_visit,
        a.fee,
        a.is_paid,
        a.status as appointment_status,
        CASE 
            WHEN a.is_paid = true THEN 'PAID'
            WHEN a.status = 'CANCELLED' THEN 'CANCELLED'
            ELSE 'OUTSTANDING'
        END as payment_status,
        a.created_at as appointment_created
    FROM appointments a
    JOIN doctors d ON a.doctor_id = d.id
    JOIN users du ON d.user_id = du.id
    JOIN doctors ds ON d.id = ds.id
    JOIN patients p ON a.patient_id = p.id
    JOIN users pu ON p.user_id = pu.id
    WHERE pu.id = patientUserId
    ORDER BY a.appointment_datetime DESC;
END//

-- 5. Get Specialization Statistics
-- Returns statistics grouped by medical specialization
DROP PROCEDURE IF EXISTS GetSpecializationStatistics//
CREATE PROCEDURE GetSpecializationStatistics(IN fromDate DATE, IN toDate DATE)
BEGIN
    SELECT 
        d.specialization,
        COUNT(DISTINCT doc.id) as total_doctors,
        COUNT(a.id) as total_appointments,
        COUNT(CASE WHEN a.status = 'COMPLETED' THEN 1 END) as completed_appointments,
        ROUND(AVG(a.fee), 2) as average_fee,
        SUM(CASE WHEN a.is_paid = true THEN a.fee ELSE 0 END) as total_revenue,
        COUNT(DISTINCT a.patient_id) as unique_patients,
        ROUND((COUNT(CASE WHEN a.status = 'COMPLETED' THEN 1 END) * 100.0) / NULLIF(COUNT(a.id), 0), 2) as completion_rate
    FROM doctors d
    JOIN doctors doc ON d.specialization = doc.specialization
    LEFT JOIN appointments a ON doc.id = a.doctor_id 
        AND a.appointment_datetime BETWEEN fromDate AND toDate
    GROUP BY d.specialization
    ORDER BY total_revenue DESC;
END//

-- 6. Get Daily Schedule for Doctor
-- Returns daily appointment schedule for a specific doctor
DROP PROCEDURE IF EXISTS GetDoctorDailySchedule//
CREATE PROCEDURE GetDoctorDailySchedule(IN doctorUserId BIGINT, IN scheduleDate DATE)
BEGIN
    SELECT 
        a.id as appointment_id,
        TIME(a.appointment_datetime) as appointment_time,
        a.duration_minutes,
        CONCAT(pu.first_name, ' ', pu.last_name) as patient_name,
        pu.email as patient_email,
        p.phone_number as patient_phone,
        a.appointment_type,
        a.reason_for_visit,
        a.status,
        a.notes,
        a.fee,
        a.is_paid,
        CASE 
            WHEN a.appointment_datetime < NOW() AND a.status = 'SCHEDULED' THEN 'OVERDUE'
            WHEN a.appointment_datetime < NOW() AND a.status = 'CONFIRMED' THEN 'OVERDUE'
            ELSE a.status
        END as current_status
    FROM appointments a
    JOIN doctors d ON a.doctor_id = d.id
    JOIN users du ON d.user_id = du.id
    JOIN patients p ON a.patient_id = p.id
    JOIN users pu ON p.user_id = pu.id
    WHERE du.id = doctorUserId 
      AND DATE(a.appointment_datetime) = scheduleDate
    ORDER BY a.appointment_datetime;
END//

-- 7. Get Outstanding Payments Report
-- Returns all unpaid appointments with patient and doctor details
DROP PROCEDURE IF EXISTS GetOutstandingPaymentsReport//
CREATE PROCEDURE GetOutstandingPaymentsReport(IN daysOverdue INT)
BEGIN
    SELECT 
        CONCAT(pu.first_name, ' ', pu.last_name) as patient_name,
        pu.email as patient_email,
        p.phone_number as patient_phone,
        p.insurance_provider,
        CONCAT(du.first_name, ' ', du.last_name) as doctor_name,
        d.specialization,
        a.appointment_datetime,
        a.appointment_type,
        a.fee,
        DATEDIFF(CURDATE(), DATE(a.appointment_datetime)) as days_overdue,
        a.status,
        a.reason_for_visit
    FROM appointments a
    JOIN patients p ON a.patient_id = p.id
    JOIN users pu ON p.user_id = pu.id
    JOIN doctors d ON a.doctor_id = d.id
    JOIN users du ON d.user_id = du.id
    WHERE a.is_paid = false 
      AND a.status IN ('COMPLETED', 'NO_SHOW')
      AND DATEDIFF(CURDATE(), DATE(a.appointment_datetime)) >= daysOverdue
    ORDER BY days_overdue DESC, a.fee DESC;
END//

-- 8. Get Appointment Type Distribution
-- Returns distribution of appointment types for reporting
DROP PROCEDURE IF EXISTS GetAppointmentTypeDistribution//
CREATE PROCEDURE GetAppointmentTypeDistribution(IN fromDate DATE, IN toDate DATE)
BEGIN
    SELECT 
        appointment_type,
        COUNT(*) as count,
        ROUND((COUNT(*) * 100.0) / (SELECT COUNT(*) FROM appointments 
                                    WHERE appointment_datetime BETWEEN fromDate AND toDate), 2) as percentage,
        AVG(fee) as average_fee,
        SUM(CASE WHEN is_paid = true THEN fee ELSE 0 END) as total_revenue,
        AVG(duration_minutes) as average_duration
    FROM appointments
    WHERE appointment_datetime BETWEEN fromDate AND toDate
    GROUP BY appointment_type
    ORDER BY count DESC;
END//

-- 9. Get Daily Appointment Report By Doctor
-- Returns daily appointment report for a specific doctor by date
DROP PROCEDURE IF EXISTS GetDailyAppointmentReportByDoctor//
CREATE PROCEDURE GetDailyAppointmentReportByDoctor(IN doctorId BIGINT, IN reportDate DATE)
BEGIN
    SELECT 
        CONCAT(du.first_name, ' ', du.last_name) as doctor_name,
        d.specialization,
        reportDate as report_date,
        COUNT(*) as total_appointments,
        COUNT(CASE WHEN a.status = 'COMPLETED' THEN 1 END) as completed_appointments,
        COUNT(CASE WHEN a.status = 'CANCELLED' THEN 1 END) as cancelled_appointments,
        COUNT(CASE WHEN a.status = 'NO_SHOW' THEN 1 END) as no_show_appointments,
        COUNT(CASE WHEN a.status = 'SCHEDULED' THEN 1 END) as scheduled_appointments,
        SUM(a.duration_minutes) as total_duration_minutes,
        AVG(a.duration_minutes) as average_duration_minutes,
        SUM(CASE WHEN a.is_paid = true THEN a.fee ELSE 0 END) as total_revenue,
        SUM(CASE WHEN a.is_paid = false THEN a.fee ELSE 0 END) as pending_revenue,
        COUNT(DISTINCT a.patient_id) as unique_patients,
        MIN(TIME(a.appointment_datetime)) as first_appointment_time,
        MAX(TIME(a.appointment_datetime)) as last_appointment_time
    FROM appointments a
    JOIN doctors d ON a.doctor_id = d.id
    JOIN users du ON d.user_id = du.id
    WHERE a.doctor_id = doctorId 
      AND DATE(a.appointment_datetime) = reportDate
    GROUP BY du.first_name, du.last_name, d.specialization
    HAVING COUNT(*) > 0;
END//

DELIMITER ;