-- Sample Data for Smart Clinic Management System
-- This file will be executed automatically by Spring Boot on startup

-- Clear existing data (in case of re-runs)
SET FOREIGN_KEY_CHECKS = 0;

-- Insert Sample Users
INSERT INTO users (username, password, email, first_name, last_name, role, active, created_at, updated_at) VALUES
('admin1', '$2a$10$9mJLKE3q6/hzCVCJ7JktB.5pQ3xb1XsS1oaT7KdPOhBb2u6pJ3L2G', 'admin@smartclinic.com', 'Super', 'Admin', 'ADMIN', true, NOW(), NOW()),
('admin2', '$2a$10$9mJLKE3q6/hzCVCJ7JktB.5pQ3xb1XsS1oaT7KdPOhBb2u6pJ3L2G', 'manager@smartclinic.com', 'Jane', 'Manager', 'ADMIN', true, NOW(), NOW()),

-- Doctors
('dr.smith', '$2a$10$9mJLKE3q6/hzCVCJ7JktB.5pQ3xb1XsS1oaT7KdPOhBb2u6pJ3L2G', 'dr.smith@smartclinic.com', 'John', 'Smith', 'DOCTOR', true, NOW(), NOW()),
('dr.johnson', '$2a$10$9mJLKE3q6/hzCVCJ7JktB.5pQ3xb1XsS1oaT7KdPOhBb2u6pJ3L2G', 'dr.johnson@smartclinic.com', 'Emily', 'Johnson', 'DOCTOR', true, NOW(), NOW()),
('dr.brown', '$2a$10$9mJLKE3q6/hzCVCJ7JktB.5pQ3xb1XsS1oaT7KdPOhBb2u6pJ3L2G', 'dr.brown@smartclinic.com', 'Michael', 'Brown', 'DOCTOR', true, NOW(), NOW()),
('dr.davis', '$2a$10$9mJLKE3q6/hzCVCJ7JktB.5pQ3xb1XsS1oaT7KdPOhBb2u6pJ3L2G', 'dr.davis@smartclinic.com', 'Sarah', 'Davis', 'DOCTOR', true, NOW(), NOW()),

-- Patients
('patient1', '$2a$10$9mJLKE3q6/hzCVCJ7JktB.5pQ3xb1XsS1oaT7KdPOhBb2u6pJ3L2G', 'alice.wilson@email.com', 'Alice', 'Wilson', 'PATIENT', true, NOW(), NOW()),
('patient2', '$2a$10$9mJLKE3q6/hzCVCJ7JktB.5pQ3xb1XsS1oaT7KdPOhBb2u6pJ3L2G', 'bob.garcia@email.com', 'Bob', 'Garcia', 'PATIENT', true, NOW(), NOW()),
('patient3', '$2a$10$9mJLKE3q6/hzCVCJ7JktB.5pQ3xb1XsS1oaT7KdPOhBb2u6pJ3L2G', 'carol.martinez@email.com', 'Carol', 'Martinez', 'PATIENT', true, NOW(), NOW()),
('patient4', '$2a$10$9mJLKE3q6/hzCVCJ7JktB.5pQ3xb1XsS1oaT7KdPOhBb2u6pJ3L2G', 'david.rodriguez@email.com', 'David', 'Rodriguez', 'PATIENT', true, NOW(), NOW()),
('patient5', '$2a$10$9mJLKE3q6/hzCVCJ7JktB.5pQ3xb1XsS1oaT7KdPOhBb2u6pJ3L2G', 'eva.lopez@email.com', 'Eva', 'Lopez', 'PATIENT', true, NOW(), NOW()),
('patient6', '$2a$10$9mJLKE3q6/hzCVCJ7JktB.5pQ3xb1XsS1oaT7KdPOhBb2u6pJ3L2G', 'frank.gonzalez@email.com', 'Frank', 'Gonzalez', 'PATIENT', true, NOW(), NOW());

-- Insert Sample Admins
INSERT INTO admins (user_id, employee_id, department, admin_level, phone_number, office_location, permissions, active, created_at, updated_at) VALUES
(1, 'EMP001', 'Administration', 'SUPER_ADMIN', '+1234567890', 'Admin Office A1', 'ALL_PERMISSIONS', true, NOW(), NOW()),
(2, 'EMP002', 'Operations', 'MANAGER', '+1234567891', 'Manager Office B2', 'MANAGE_USERS,MANAGE_APPOINTMENTS,VIEW_REPORTS', true, NOW(), NOW());

-- Insert Sample Doctors
INSERT INTO doctors (user_id, license_number, specialization, phone_number, qualifications, years_of_experience, office_address, consultation_fee, available, created_at, updated_at) VALUES
(3, 'MD123456', 'Cardiology', '+1234567892', 'MD from Harvard Medical School, Board Certified Cardiologist', 15, '123 Medical Center Dr, Suite 300', 250.00, true, NOW(), NOW()),
(4, 'MD234567', 'Pediatrics', '+1234567893', 'MD from Johns Hopkins, Pediatric Specialist', 12, '123 Medical Center Dr, Suite 301', 200.00, true, NOW(), NOW()),
(5, 'MD345678', 'Orthopedics', '+1234567894', 'MD from Mayo Clinic, Orthopedic Surgeon', 20, '123 Medical Center Dr, Suite 302', 300.00, true, NOW(), NOW()),
(6, 'MD456789', 'Dermatology', '+1234567895', 'MD from Stanford Medical, Dermatology Specialist', 8, '123 Medical Center Dr, Suite 303', 180.00, true, NOW(), NOW());

-- Insert Sample Patients
INSERT INTO patients (user_id, date_of_birth, gender, phone_number, address, city, state, zip_code, emergency_contact_name, emergency_contact_phone, insurance_provider, insurance_policy_number, medical_notes, active, created_at, updated_at) VALUES
(7, '1985-03-15', 'Female', '+1234567896', '456 Oak Street', 'Springfield', 'IL', '62701', 'John Wilson', '+1234567900', 'Blue Cross Blue Shield', 'BC12345678', 'Allergic to penicillin', true, NOW(), NOW()),
(8, '1978-11-22', 'Male', '+1234567897', '789 Pine Avenue', 'Springfield', 'IL', '62702', 'Maria Garcia', '+1234567901', 'Aetna', 'AET987654321', 'History of hypertension', true, NOW(), NOW()),
(9, '1992-07-08', 'Female', '+1234567898', '321 Elm Drive', 'Springfield', 'IL', '62703', 'Carlos Martinez', '+1234567902', 'Cigna', 'CIG456789123', 'No known allergies', true, NOW(), NOW()),
(10, '1965-12-03', 'Male', '+1234567899', '654 Maple Lane', 'Springfield', 'IL', '62704', 'Ana Rodriguez', '+1234567903', 'UnitedHealthcare', 'UHC321654987', 'Diabetic, requires insulin', true, NOW(), NOW()),
(11, '1988-09-18', 'Female', '+1234567810', '987 Birch Street', 'Springfield', 'IL', '62705', 'Luis Lopez', '+1234567904', 'Kaiser Permanente', 'KP654321789', 'Previous surgery on left knee', true, NOW(), NOW()),
(12, '1995-04-25', 'Male', '+1234567811', '147 Cedar Road', 'Springfield', 'IL', '62706', 'Isabella Gonzalez', '+1234567905', 'Blue Cross Blue Shield', 'BC789456123', 'Asthmatic, uses inhaler', true, NOW(), NOW());

-- Insert Sample Appointments
INSERT INTO appointments (doctor_id, patient_id, appointment_datetime, duration_minutes, status, appointment_type, reason_for_visit, notes, doctor_notes, fee, is_paid, created_at, updated_at) VALUES
-- Past appointments (completed)
(1, 1, '2024-01-15 09:00:00', 60, 'COMPLETED', 'CONSULTATION', 'Chest pain evaluation', 'Patient reports occasional chest pain during exercise', 'ECG normal, stress test recommended', 250.00, true, NOW(), NOW()),
(2, 2, '2024-01-16 10:30:00', 45, 'COMPLETED', 'CHECK_UP', 'Annual pediatric checkup', 'Routine annual examination', 'All vitals normal, vaccines up to date', 200.00, true, NOW(), NOW()),
(1, 3, '2024-01-18 14:00:00', 60, 'COMPLETED', 'FOLLOW_UP', 'Follow-up for cardiac evaluation', 'Return visit after stress test', 'Stress test results normal, continue current medication', 250.00, true, NOW(), NOW()),

-- Recent appointments (some completed, some scheduled)
(3, 4, '2024-02-01 11:00:00', 90, 'COMPLETED', 'CONSULTATION', 'Knee pain assessment', 'Patient reports persistent knee pain after fall', 'X-ray shows minor inflammation, prescribed anti-inflammatory', 300.00, true, NOW(), NOW()),
(4, 5, '2024-02-05 09:30:00', 30, 'COMPLETED', 'CONSULTATION', 'Skin rash evaluation', 'Patient has persistent rash on arms', 'Diagnosed eczema, prescribed topical cream', 180.00, false, NOW(), NOW()),
(2, 6, '2024-02-10 15:00:00', 45, 'COMPLETED', 'VACCINATION', 'Annual flu shot', 'Routine vaccination appointment', 'Flu vaccine administered, no adverse reactions', 50.00, true, NOW(), NOW()),

-- Future appointments (scheduled)
(1, 2, '2024-12-20 10:00:00', 60, 'SCHEDULED', 'FOLLOW_UP', 'Hypertension follow-up', 'Monitor blood pressure medication effectiveness', NULL, 250.00, false, NOW(), NOW()),
(2, 1, '2024-12-22 09:15:00', 30, 'CONFIRMED', 'CHECK_UP', 'Routine health screening', 'Annual health maintenance visit', NULL, 200.00, false, NOW(), NOW()),
(3, 3, '2024-12-23 13:30:00', 60, 'SCHEDULED', 'CONSULTATION', 'Hip pain evaluation', 'New patient complaint of hip pain', NULL, 300.00, false, NOW(), NOW()),
(4, 4, '2024-12-24 11:45:00', 45, 'SCHEDULED', 'FOLLOW_UP', 'Skin condition follow-up', 'Check progress of eczema treatment', NULL, 180.00, false, NOW(), NOW()),

-- Some cancelled/no-show appointments for reporting
(1, 5, '2024-02-08 14:30:00', 60, 'CANCELLED', 'CONSULTATION', 'Chest pain consultation', 'Patient cancelled due to conflict', NULL, 250.00, false, NOW(), NOW()),
(2, 3, '2024-02-12 16:00:00', 30, 'NO_SHOW', 'CHECK_UP', 'Routine checkup', 'Patient did not show up', NULL, 200.00, false, NOW(), NOW());

SET FOREIGN_KEY_CHECKS = 1;