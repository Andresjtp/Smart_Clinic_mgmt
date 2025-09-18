# Module 3: Sample Data and Stored Procedures

## Overview
This module implements the database setup, sample data insertion, and stored procedures for the Smart Clinic Management System. It includes configuration for both MySQL (relational) and MongoDB (document) databases with comprehensive sample datasets and reporting procedures.

---

## Database Configuration

### MySQL Configuration
The system uses MySQL for structured relational data with the following configuration:

**Database**: `smart_clinic_db`
**Connection Settings** (from `application.properties`):
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/smart_clinic_db?createDatabaseIfNotExist=true&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=password
spring.jpa.hibernate.ddl-auto=create-drop
spring.sql.init.mode=always
spring.sql.init.data-locations=classpath:data.sql
```

### MongoDB Configuration
MongoDB is used for flexible document storage:

**Database**: `smart_clinic_mongo_db`
**Connection Settings**:
```properties
spring.data.mongodb.host=localhost
spring.data.mongodb.port=27017
spring.data.mongodb.database=smart_clinic_mongo_db
spring.data.mongodb.auto-index-creation=true
```

---

## Sample Data Overview

### MySQL Sample Data (`data.sql`)

#### 1. Users (12 total)
- **2 Admins**: Super Admin and Manager
- **4 Doctors**: Specialists in Cardiology, Pediatrics, Orthopedics, Dermatology
- **6 Patients**: Diverse demographic with various medical conditions

**Sample User Data**:
```sql
-- Admin Users
('admin1', 'encrypted_password', 'admin@smartclinic.com', 'Super', 'Admin', 'ADMIN')
('admin2', 'encrypted_password', 'manager@smartclinic.com', 'Jane', 'Manager', 'ADMIN')

-- Doctor Users  
('dr.smith', 'encrypted_password', 'dr.smith@smartclinic.com', 'John', 'Smith', 'DOCTOR')
('dr.johnson', 'encrypted_password', 'dr.johnson@smartclinic.com', 'Emily', 'Johnson', 'DOCTOR')

-- Patient Users
('patient1', 'encrypted_password', 'alice.wilson@email.com', 'Alice', 'Wilson', 'PATIENT')
```

#### 2. Doctors (4 specialists)
```sql
-- Dr. John Smith - Cardiologist
(user_id=3, license='MD123456', specialization='Cardiology', fee=250.00)

-- Dr. Emily Johnson - Pediatrician  
(user_id=4, license='MD234567', specialization='Pediatrics', fee=200.00)

-- Dr. Michael Brown - Orthopedic Surgeon
(user_id=5, license='MD345678', specialization='Orthopedics', fee=300.00)

-- Dr. Sarah Davis - Dermatologist
(user_id=6, license='MD456789', specialization='Dermatology', fee=180.00)
```

#### 3. Patients (6 with diverse profiles)
```sql
-- Alice Wilson (Female, 39, Springfield IL)
(user_id=7, dob='1985-03-15', insurance='Blue Cross Blue Shield', allergy='penicillin')

-- David Rodriguez (Male, 59, Springfield IL)  
(user_id=10, dob='1965-12-03', insurance='UnitedHealthcare', condition='Diabetic')

-- Frank Gonzalez (Male, 29, Springfield IL)
(user_id=12, dob='1995-04-25', insurance='Blue Cross Blue Shield', condition='Asthmatic')
```

#### 4. Appointments (12 total)
- **6 Completed** appointments (past dates)
- **3 Scheduled/Confirmed** future appointments
- **2 Cancelled/No-Show** for reporting scenarios
- **1 Follow-up** appointment

**Appointment Distribution by Type**:
- Consultations: 5
- Follow-ups: 3  
- Check-ups: 2
- Vaccinations: 1
- Procedures: 1

#### 5. Admins (2 with different levels)
```sql
-- Super Admin
(user_id=1, employee_id='EMP001', department='Administration', level='SUPER_ADMIN')

-- Manager
(user_id=2, employee_id='EMP002', department='Operations', level='MANAGER')
```

### MongoDB Sample Data (`MongoDataInitializer.java`)

#### 1. Prescriptions Collection (3 documents)

**Prescription 1 - Diabetes Management**:
```json
{
  "patient_id": 4,
  "doctor_id": 1,
  "appointment_id": 4,
  "prescription_date": "2024-02-01",
  "medications": [
    {
      "name": "Lisinopril",
      "dosage": "10mg", 
      "frequency": "Once daily",
      "durationDays": 30,
      "instructions": "Take with food in the morning"
    },
    {
      "name": "Metformin",
      "dosage": "500mg",
      "frequency": "Twice daily", 
      "durationDays": 30,
      "instructions": "Take with meals"
    }
  ]
}
```

**Prescription 2 - Asthma Treatment**:
```json
{
  "patient_id": 6,
  "doctor_id": 2, 
  "appointment_id": 6,
  "medications": [
    {
      "name": "Albuterol Inhaler",
      "dosage": "90mcg",
      "frequency": "As needed",
      "instructions": "Use for shortness of breath, max 4 times daily"
    }
  ]
}
```

**Prescription 3 - Dermatological Treatment**:
```json
{
  "patient_id": 5,
  "doctor_id": 4,
  "medications": [
    {
      "name": "Hydrocortisone Cream", 
      "dosage": "1%",
      "frequency": "Twice daily",
      "instructions": "Apply thin layer to affected areas"
    }
  ]
}
```

#### 2. Medical History Collection (4 documents)

**Medical History for Alice Wilson (Patient 1)**:
- Cardiac evaluation records
- Follow-up after stress test
- Complete vital signs tracking

**Medical History for David Rodriguez (Patient 4)**:
- Diabetes management records
- Blood pressure monitoring
- Medication compliance tracking

**Medical History for Frank Gonzalez (Patient 6)**:
- Asthma management history
- Vaccination records
- Chronic condition monitoring

**Medical History for Eva Lopez (Patient 5)**:
- Dermatological consultation
- Eczema diagnosis and treatment
- Skin care management

---

## Stored Procedures

### 1. Patient Reports

#### `GetPatientSummaryReport(patientUserId)`
Comprehensive patient overview including:
- Personal and contact information
- Insurance details
- Appointment statistics (total, completed, cancelled, no-shows)
- Financial summary (paid amounts, outstanding balance)
- Last appointment date

**Usage Example**:
```sql
CALL GetPatientSummaryReport(7); -- Alice Wilson's summary
```

#### `GetPatientPaymentHistory(patientUserId)`
Detailed payment history with:
- All appointments with dates and doctors
- Payment status for each appointment
- Outstanding balances
- Appointment types and reasons

### 2. Doctor Analytics  

#### `GetDoctorPerformanceReport(doctorUserId, fromDate, toDate)`
Doctor performance metrics including:
- Appointment statistics and completion rates
- Revenue generation and average fees
- Unique patient count
- Cancellation and no-show rates

**Usage Example**:
```sql
CALL GetDoctorPerformanceReport(3, '2024-01-01', '2024-12-31'); -- Dr. Smith's annual report
```

#### `GetDoctorDailySchedule(doctorUserId, scheduleDate)`
Daily schedule with:
- Chronological appointment listing
- Patient contact information
- Appointment types and status
- Overdue appointment identification

### 3. Administrative Reports

#### `GetMonthlyAppointmentAnalytics(reportYear, reportMonth)`
Monthly clinic statistics:
- Total appointments by status
- Revenue and payment analytics  
- Appointment type distribution
- Unique patients and active doctors

#### `GetOutstandingPaymentsReport(daysOverdue)`
Financial management report:
- Unpaid appointments beyond specified days
- Patient and insurance information
- Total outstanding amounts
- Payment priority ranking

#### `GetSpecializationStatistics(fromDate, toDate)`
Specialization performance analysis:
- Doctor count by specialty
- Appointment volumes and completion rates
- Revenue by medical specialty
- Patient distribution

#### `GetAppointmentTypeDistribution(fromDate, toDate)`
Service analysis report:
- Appointment type percentages
- Average fees by service type
- Revenue distribution
- Duration analytics

### 4. Operational Queries

**Sample Procedure Calls**:
```sql
-- Monthly analytics for February 2024
CALL GetMonthlyAppointmentAnalytics(2024, 2);

-- Outstanding payments over 30 days
CALL GetOutstandingPaymentsReport(30);

-- Specialization stats for Q1 2024  
CALL GetSpecializationStatistics('2024-01-01', '2024-03-31');

-- Dr. Smith's schedule for today
CALL GetDoctorDailySchedule(3, CURDATE());

-- Appointment distribution for 2024
CALL GetAppointmentTypeDistribution('2024-01-01', '2024-12-31');
```

---

## Data Validation and Integrity

### MySQL Constraints
- **Unique Constraints**: usernames, emails, license numbers, employee IDs
- **Foreign Key Constraints**: Proper referential integrity between tables
- **Check Constraints**: Valid enums for roles, statuses, and types
- **Not Null Constraints**: Required fields properly enforced

### MongoDB Validation
- **Schema Validation**: Using Java annotations on document models
- **Cross-Reference Validation**: Patient/Doctor/Appointment IDs link to MySQL
- **Data Type Validation**: Proper LocalDateTime, LocalDate formatting
- **Nested Document Validation**: Medications and MedicalRecords validated

### Sample Data Quality
- **Realistic Data**: Names, addresses, phone numbers follow realistic patterns
- **Medical Accuracy**: Conditions, medications, and treatments are medically appropriate  
- **Temporal Consistency**: Appointment dates, prescription dates align logically
- **Financial Integrity**: Fee calculations and payment statuses are consistent

---

## Database Performance Considerations

### Indexing Strategy
- **Primary Keys**: Auto-indexed on all ID fields
- **Unique Constraints**: Automatic indexes on username, email, license_number
- **Foreign Keys**: Indexed for optimal join performance
- **MongoDB**: Auto-indexing enabled for document collections

### Query Optimization
- **Stored Procedures**: Pre-compiled for faster execution
- **Aggregation**: Complex calculations done at database level
- **Lazy Loading**: JPA configured for optimal memory usage
- **Connection Pooling**: MySQL connection pool configured

---

## Security Considerations

### Data Protection
- **Password Encryption**: BCrypt hashing for all user passwords
- **Sensitive Data**: Medical notes and prescription data properly secured
- **HIPAA Compliance**: Patient data properly isolated and protected
- **Role-Based Access**: User roles enforce data access permissions

### Audit Trail
- **Timestamps**: All records include created_at and updated_at
- **User Tracking**: All data modifications linked to specific users
- **Status Tracking**: Appointment and payment status changes tracked

---

## Testing the Implementation

### MySQL Data Verification
```sql
-- Verify user counts by role
SELECT role, COUNT(*) FROM users GROUP BY role;

-- Check appointment distribution  
SELECT status, COUNT(*) FROM appointments GROUP BY status;

-- Validate doctor specializations
SELECT specialization, COUNT(*) FROM doctors GROUP BY specialization;
```

### MongoDB Data Verification
```javascript
// In MongoDB shell
db.prescriptions.count()
db.medical_history.count()
db.prescriptions.findOne()
```

### Stored Procedure Testing
```sql
-- Test all procedures with sample data
CALL GetPatientSummaryReport(7);
CALL GetDoctorPerformanceReport(3, '2024-01-01', '2024-12-31');
CALL GetMonthlyAppointmentAnalytics(2024, 2);
```

---

## Next Steps

With Module 3 completed, the database foundation is ready for:

- **Module 4**: Repository layers and service implementations
- **Module 5**: REST API controllers and business logic
- **Module 6**: Frontend development and user interfaces
- **Module 7**: Testing, security, and deployment

The sample data provides a realistic foundation for development and testing, while the stored procedures enable comprehensive reporting and analytics capabilities for the Smart Clinic Management System.

---

## Files Created

```
src/main/resources/
├── application.properties          # Database configuration
├── data.sql                       # MySQL sample data
└── stored_procedures.sql          # Reporting procedures

src/main/java/.../config/
└── MongoDataInitializer.java     # MongoDB sample data loader
```

**Total Records Created**:
- MySQL: 12 Users, 4 Doctors, 6 Patients, 2 Admins, 12 Appointments
- MongoDB: 3 Prescriptions, 4 Medical Histories (with multiple records each)
- Stored Procedures: 8 comprehensive reporting procedures