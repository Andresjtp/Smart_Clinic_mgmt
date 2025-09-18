# Module 2: Database Design and JPA Models

## Overview
This module covers the database design and implementation of JPA models for the Smart Clinic Management System. The system uses both MySQL (relational) for structured data and MongoDB (document-based) for flexible data.

## Relational Database (MySQL) - Entity Relationship Design

### Core Entities

#### 1. User Entity
The central entity for authentication and role management.

```java
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50)
    @Column(unique = true, nullable = false)
    private String username;
    
    @NotBlank(message = "Password is required")
    @Size(min = 6)
    @Column(nullable = false)
    private String password;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    @Column(unique = true, nullable = false)
    private String email;
    
    @NotNull(message = "Role is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role; // ADMIN, DOCTOR, PATIENT
    
    // Additional fields: firstName, lastName, active, timestamps
}
```

#### 2. Doctor Entity
Contains doctor-specific information with one-to-one relationship to User.

```java
@Entity
@Table(name = "doctors")
public class Doctor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User user;
    
    @NotBlank(message = "License number is required")
    @Column(name = "license_number", unique = true, nullable = false)
    private String licenseNumber;
    
    @NotBlank(message = "Specialization is required")
    @Column(nullable = false)
    private String specialization;
    
    // Additional fields: phoneNumber, qualifications, yearsOfExperience, etc.
}
```

#### 3. Patient Entity
Contains patient-specific information with one-to-one relationship to User.

```java
@Entity
@Table(name = "patients")
public class Patient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User user;
    
    @Past(message = "Date of birth must be in the past")
    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;
    
    @Pattern(regexp = "^[+]?[1-9][0-9]{7,14}$")
    @Column(name = "phone_number")
    private String phoneNumber;
    
    // Additional fields: gender, address, emergencyContact, insurance, etc.
}
```

#### 4. Admin Entity
Contains admin-specific information with one-to-one relationship to User.

```java
@Entity
@Table(name = "admins")
public class Admin {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User user;
    
    @NotBlank(message = "Employee ID is required")
    @Column(name = "employee_id", unique = true, nullable = false)
    private String employeeId;
    
    @NotNull(message = "Admin level is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "admin_level", nullable = false)
    private AdminLevel adminLevel; // SUPER_ADMIN, MANAGER, STAFF
}
```

#### 5. Appointment Entity
Manages appointments between doctors and patients.

```java
@Entity
@Table(name = "appointments")
public class Appointment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;
    
    @NotNull(message = "Appointment date and time is required")
    @Future(message = "Appointment must be scheduled for a future date and time")
    @Column(name = "appointment_datetime", nullable = false)
    private LocalDateTime appointmentDateTime;
    
    @NotNull(message = "Appointment status is required")
    @Enumerated(EnumType.STRING)
    private AppointmentStatus status; // SCHEDULED, CONFIRMED, COMPLETED, etc.
    
    @NotNull(message = "Appointment type is required")
    @Enumerated(EnumType.STRING)
    private AppointmentType appointmentType; // CONSULTATION, FOLLOW_UP, etc.
}
```

### Entity Relationships Summary
- **User** (1:1) **Doctor/Patient/Admin** - One user can have one role-specific profile
- **Doctor** (1:*) **Appointment** (*:1) **Patient** - Many-to-many through appointments
- All entities have audit fields (createdAt, updatedAt) using Hibernate annotations

---

## Document Database (MongoDB) - Collections

### 1. Prescriptions Collection
Stores flexible prescription data linked to MySQL entities.

```java
@Document(collection = "prescriptions")
public class Prescription {
    @Id
    private String id;
    
    @NotNull(message = "Patient ID is required")
    @Field("patient_id")
    private Long patientId; // Reference to MySQL Patient
    
    @NotNull(message = "Doctor ID is required")
    @Field("doctor_id")
    private Long doctorId; // Reference to MySQL Doctor
    
    @Field("appointment_id")
    private Long appointmentId; // Reference to MySQL Appointment
    
    @NotEmpty(message = "At least one medication is required")
    private List<Medication> medications; // Embedded documents
    
    @Field("prescription_date")
    private LocalDate prescriptionDate;
    
    // Additional fields: instructions, notes, isActive
}
```

#### Medication Embedded Document
```java
public class Medication {
    @NotBlank(message = "Medication name is required")
    private String name;
    
    @NotBlank(message = "Dosage is required")
    private String dosage;
    
    @NotBlank(message = "Frequency is required")
    private String frequency;
    
    @NotNull(message = "Duration is required")
    @Min(value = 1, message = "Duration must be at least 1 day")
    private Integer durationDays;
    
    // Additional fields: instructions, unit, quantity
}
```

### 2. Medical History Collection
Stores comprehensive patient medical records.

```java
@Document(collection = "medical_history")
public class MedicalHistory {
    @Id
    private String id;
    
    @NotNull(message = "Patient ID is required")
    @Field("patient_id")
    private Long patientId; // Reference to MySQL Patient
    
    @NotEmpty(message = "At least one medical record is required")
    private List<MedicalRecord> records; // Embedded documents
}
```

#### MedicalRecord Embedded Document
```java
public class MedicalRecord {
    @NotNull(message = "Record date is required")
    private LocalDateTime recordDate;
    
    @NotNull(message = "Record type is required")
    @Enumerated(EnumType.STRING)
    private RecordType recordType; // CONSULTATION, DIAGNOSIS, LAB_RESULT, etc.
    
    @NotBlank(message = "Title is required")
    private String title;
    
    @NotBlank(message = "Description is required")
    private String description;
    
    @NotNull(message = "Doctor ID is required")
    private Long doctorId; // Reference to MySQL Doctor
    
    private VitalSigns vitalSigns; // Embedded vital signs
    private List<String> symptoms;
    private List<String> attachments;
    
    // Additional fields: diagnosis, treatment, notes, severity
}
```

#### VitalSigns Embedded Document
```java
public class VitalSigns {
    @DecimalMin(value = "80.0")
    @DecimalMax(value = "250.0")
    private Double systolicPressure;
    
    @DecimalMin(value = "40.0")
    @DecimalMax(value = "150.0")
    private Double diastolicPressure;
    
    @DecimalMin(value = "30.0")
    @DecimalMax(value = "220.0")
    private Double heartRate;
    
    @DecimalMin(value = "10.0")
    @DecimalMax(value = "60.0")
    private Double respiratoryRate;
    
    @DecimalMin(value = "30.0")
    @DecimalMax(value = "45.0")
    private Double temperature;
    
    // Additional fields: oxygenSaturation, weight, height, bmi
    
    public void calculateBmi() {
        if (height != null && weight != null && height > 0) {
            double heightInMeters = height / 100.0;
            this.bmi = Math.round((weight / (heightInMeters * heightInMeters)) * 100.0) / 100.0;
        }
    }
}
```

---

## Enums Used

### Role Enum
```java
public enum Role {
    ADMIN("Admin"),
    DOCTOR("Doctor"), 
    PATIENT("Patient");
}
```

### AdminLevel Enum
```java
public enum AdminLevel {
    SUPER_ADMIN("Super Admin"),
    MANAGER("Manager"),
    STAFF("Staff");
}
```

### AppointmentStatus Enum
```java
public enum AppointmentStatus {
    SCHEDULED("Scheduled"),
    CONFIRMED("Confirmed"),
    IN_PROGRESS("In Progress"),
    COMPLETED("Completed"),
    CANCELLED("Cancelled"),
    NO_SHOW("No Show"),
    RESCHEDULED("Rescheduled");
}
```

### AppointmentType Enum
```java
public enum AppointmentType {
    CONSULTATION("Consultation"),
    FOLLOW_UP("Follow-up"),
    CHECK_UP("Check-up"),
    EMERGENCY("Emergency"),
    PROCEDURE("Procedure"),
    VACCINATION("Vaccination"),
    SCREENING("Screening"),
    THERAPY("Therapy");
}
```

### RecordType Enum (MongoDB)
```java
public enum RecordType {
    CONSULTATION("Consultation"),
    DIAGNOSIS("Diagnosis"),
    TREATMENT("Treatment"),
    PRESCRIPTION("Prescription"),
    LAB_RESULT("Lab Result"),
    IMAGING("Imaging"),
    SURGERY("Surgery"),
    VACCINATION("Vaccination"),
    ALLERGY("Allergy"),
    CHRONIC_CONDITION("Chronic Condition"),
    EMERGENCY("Emergency"),
    FOLLOW_UP("Follow-up"),
    DISCHARGE("Discharge"),
    REFERRAL("Referral");
}
```

---

## Validation Features Implemented

1. **Field-level Validations**:
   - `@NotNull`, `@NotBlank`, `@NotEmpty` for required fields
   - `@Size` for string length constraints
   - `@Email` for email format validation
   - `@Pattern` for regex validation (phone numbers, ZIP codes)
   - `@Past`, `@Future` for date validations
   - `@Min`, `@Max` for numeric range validations
   - `@DecimalMin`, `@DecimalMax` for decimal range validations
   - `@Digits` for decimal precision validation

2. **Entity-level Features**:
   - Unique constraints on usernames, emails, license numbers
   - Proper foreign key relationships with cascading
   - Audit timestamps using Hibernate annotations
   - Enum validation for controlled vocabularies

3. **MongoDB Flexibility**:
   - Flexible schema for medical records and prescriptions
   - Embedded documents for related data
   - Cross-reference to MySQL entities via ID fields

---

## Key Design Decisions

1. **Hybrid Database Approach**:
   - MySQL for structured, relational data (users, appointments)
   - MongoDB for flexible, document-based data (medical records, prescriptions)

2. **Security Considerations**:
   - Password fields properly annotated but will be encrypted in service layer
   - Sensitive medical information flagged with `isConfidential`
   - Role-based access control through User entity

3. **Performance Optimizations**:
   - Lazy loading for entity relationships
   - Indexed fields (unique constraints automatically indexed)
   - Embedded documents in MongoDB to reduce joins

4. **Extensibility**:
   - Enum-based status and type fields for easy extension
   - MongoDB collections allow adding new fields without schema changes
   - Modular entity design allows adding new roles/entities

---

## Next Steps
- Module 3: Implement Repository layers and REST APIs
- Module 4: Create frontend components
- Module 5: Add comprehensive testing and deployment configurations
