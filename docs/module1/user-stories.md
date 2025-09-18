# User Stories
## Smart Clinic Management System

### Document Information
- **Project**: Smart Clinic Management System
- **Module**: 1 - Project Overview and Requirements Gathering  
- **Date**: September 17, 2025
- **Version**: 1.0

---

## User Roles and Permissions

### Administrator Role
**Description**: System administrators who manage the overall clinic management system, user accounts, and system configuration.

**Permissions**:
- Full system access and configuration
- User account management (create, edit, delete, assign roles)
- System monitoring and maintenance
- Generate comprehensive reports
- Manage clinic settings and configurations
- Access audit logs and security settings

### Doctor Role
**Description**: Medical practitioners who use the system to manage patient care, appointments, and medical records.

**Permissions**:
- View and manage assigned patients
- Access and update patient medical records
- Manage their own appointment schedule
- Create and modify prescriptions
- View lab results and medical history
- Generate patient reports

### Patient Role  
**Description**: Patients who use the system to manage their healthcare interactions with the clinic.

**Permissions**:
- View their own medical records and history
- Book and manage their appointments
- View their prescriptions and lab results
- Update personal contact information
- View appointment history
- Access their medical documents

---

## Epic 1: User Authentication and Authorization

### Story 1.1: User Registration
**As a** new user (Patient)  
**I want to** create an account in the clinic management system  
**So that** I can access clinic services online

**Acceptance Criteria**:
- User can register with email, password, and basic personal information
- Email verification is required before account activation
- Password must meet security requirements (8+ characters, special characters)
- System validates email uniqueness
- User receives confirmation email upon successful registration
- Account is created with Patient role by default

**Priority**: High  
**Story Points**: 5

### Story 1.2: User Login
**As a** registered user  
**I want to** log into the system securely  
**So that** I can access my personalized dashboard and features

**Acceptance Criteria**:
- User can log in with email and password
- System validates credentials against the database
- JWT token is generated upon successful authentication
- User is redirected to role-appropriate dashboard
- Failed login attempts are logged for security
- Account is locked after 5 consecutive failed attempts

**Priority**: High  
**Story Points**: 3

### Story 1.3: Role-Based Access Control
**As an** Administrator  
**I want to** assign appropriate roles to users  
**So that** they can access only the features relevant to their role

**Acceptance Criteria**:
- Admin can assign roles (Admin, Doctor, Patient) to users
- System enforces role-based access to different features
- Users see only menu items and pages relevant to their role
- API endpoints are protected based on user roles
- Role changes take effect immediately upon saving
- Audit trail is maintained for role changes

**Priority**: High  
**Story Points**: 8

---

## Epic 2: Patient Management

### Story 2.1: Patient Registration (Admin/Doctor)
**As an** Administrator or Doctor  
**I want to** register new patients in the system  
**So that** their information is available for appointments and medical records

**Acceptance Criteria**:
- Admin/Doctor can create patient profiles with complete demographics
- Required fields: name, date of birth, contact information, insurance details
- Optional fields: emergency contact, medical allergies, current medications
- System generates unique patient ID automatically
- Patient information is validated for completeness and accuracy
- Created patient receives welcome email with login instructions

**Priority**: High  
**Story Points**: 8

### Story 2.2: View Patient Profile
**As a** Patient  
**I want to** view my personal and medical information  
**So that** I can verify accuracy and stay informed about my healthcare data

**Acceptance Criteria**:
- Patient can view their complete profile information
- Personal details are displayed in an organized, readable format
- Medical history and current medications are clearly listed
- Insurance information and emergency contacts are visible
- Profile shows last update date and by whom
- Sensitive information is appropriately protected

**Priority**: Medium  
**Story Points**: 3

### Story 2.3: Update Patient Information
**As a** Patient  
**I want to** update my personal contact information  
**So that** the clinic has my current details

**Acceptance Criteria**:
- Patient can edit contact information (phone, address, email)
- Changes require confirmation before saving
- System validates new information format (email, phone number)
- Updated information is immediately reflected in the system
- Change history is maintained for audit purposes
- Patient receives confirmation of successful updates

**Priority**: Medium  
**Story Points**: 5

---

## Epic 3: Appointment Management

### Story 3.1: Book Appointment (Patient)
**As a** Patient  
**I want to** book appointments with available doctors  
**So that** I can receive medical care at a convenient time

**Acceptance Criteria**:
- Patient can view doctor availability by date and time
- System shows only available appointment slots
- Patient can select doctor, date, time, and appointment type
- Booking confirmation is sent via email
- System prevents double-booking of time slots
- Patient can add notes or reason for visit

**Priority**: High  
**Story Points**: 8

### Story 3.2: View Appointment Schedule (Doctor)
**As a** Doctor  
**I want to** view my daily and weekly appointment schedule  
**So that** I can manage my time and prepare for patient visits

**Acceptance Criteria**:
- Doctor sees calendar view of appointments (daily, weekly, monthly)
- Appointments show patient name, time, type, and notes
- Color coding for different appointment types
- Doctor can click on appointments for patient details
- Schedule automatically updates with new bookings
- Print option available for daily schedule

**Priority**: High  
**Story Points**: 5

### Story 3.3: Reschedule Appointment
**As a** Patient  
**I want to** reschedule my existing appointments  
**So that** I can change the time if my schedule changes

**Acceptance Criteria**:
- Patient can view their upcoming appointments
- System shows alternative available time slots
- Rescheduling requires confirmation from patient
- Doctor and clinic are notified of schedule changes
- Original appointment is cancelled automatically
- Rescheduling history is maintained

**Priority**: Medium  
**Story Points**: 6

### Story 3.4: Cancel Appointment
**As a** Patient  
**I want to** cancel appointments I cannot attend  
**So that** the time slot becomes available for other patients

**Acceptance Criteria**:
- Patient can cancel appointments up to 24 hours in advance
- Cancellation requires confirmation
- Doctor and clinic are notified automatically
- Time slot becomes available for rebooking
- Cancellation reason can be provided optionally
- Frequent cancellations are flagged for admin review

**Priority**: Medium  
**Story Points**: 4

---

## Epic 4: Medical Records Management

### Story 4.1: Create Medical Record (Doctor)
**As a** Doctor  
**I want to** create medical records after patient visits  
**So that** patient care history is properly documented

**Acceptance Criteria**:
- Doctor can create new medical records linked to appointments
- Records include diagnosis, treatment, medications, and notes
- System supports structured data entry and free-text notes
- Medical codes (ICD-10) can be associated with diagnoses
- Records are automatically timestamped and attributed to doctor
- Draft records can be saved and completed later

**Priority**: High  
**Story Points**: 10

### Story 4.2: View Medical History (Patient)
**As a** Patient  
**I want to** view my complete medical history  
**So that** I can track my health progress and share information with other healthcare providers

**Acceptance Criteria**:
- Patient sees chronological list of medical records
- Records show visit date, doctor, diagnosis, and treatments
- Prescription history is included and easily accessible
- Lab results are integrated and clearly presented
- Records can be filtered by date range or condition
- Print and export options are available

**Priority**: High  
**Story Points**: 6

### Story 4.3: Update Medical Record (Doctor)
**As a** Doctor  
**I want to** update or add to existing medical records  
**So that** patient information remains current and accurate

**Acceptance Criteria**:
- Doctor can edit their own medical record entries
- Changes are tracked with timestamps and user attribution
- Amendment notes can be added to explain changes
- Original information is preserved for audit trail
- Updated records are immediately visible to authorized users
- Notifications sent to relevant parties for significant changes

**Priority**: Medium  
**Story Points**: 7

---

## Epic 5: Prescription Management

### Story 5.1: Create Prescription (Doctor)
**As a** Doctor  
**I want to** create digital prescriptions for patients  
**So that** medication instructions are clear and trackable

**Acceptance Criteria**:
- Doctor can search and select medications from database
- Prescription includes medication, dosage, frequency, and duration
- Drug interaction warnings are displayed automatically
- Patient allergy alerts are shown before prescribing
- Prescriptions are linked to medical records and appointments
- Electronic signature or approval is required

**Priority**: High  
**Story Points**: 9

### Story 5.2: View Prescriptions (Patient)
**As a** Patient  
**I want to** view my current and past prescriptions  
**So that** I can manage my medications properly

**Acceptance Criteria**:
- Patient sees list of all prescriptions (current and historical)
- Prescription details include medication, dosage, and instructions
- Refill information and expiration dates are clearly shown
- Patient can mark medications as taken/not taken
- Print option available for pharmacy visits
- Prescription status (active, expired, discontinued) is indicated

**Priority**: Medium  
**Story Points**: 4

---

## Epic 6: Administrative Functions

### Story 6.1: User Management (Admin)
**As an** Administrator  
**I want to** manage user accounts and permissions  
**So that** the system remains secure and properly configured

**Acceptance Criteria**:
- Admin can create, edit, and deactivate user accounts
- User roles and permissions can be assigned and modified
- Account status and activity can be monitored
- Password resets can be initiated for users
- User access logs are available for security review
- Bulk user operations are supported

**Priority**: Medium  
**Story Points**: 8

### Story 6.2: System Reports (Admin)
**As an** Administrator  
**I want to** generate reports on system usage and clinic operations  
**So that** I can monitor performance and make informed decisions

**Acceptance Criteria**:
- Admin can generate various reports (appointments, users, medical records)
- Reports can be filtered by date range, user type, or department
- Export functionality in multiple formats (PDF, Excel, CSV)
- Scheduled reports can be automatically generated and emailed
- Dashboard shows key performance indicators
- Custom report builder for specific needs

**Priority**: Low  
**Story Points**: 10

### Story 6.3: System Configuration (Admin)
**As an** Administrator  
**I want to** configure system settings and preferences  
**So that** the system operates according to clinic policies

**Acceptance Criteria**:
- Admin can configure appointment booking rules and restrictions
- System notifications and email templates can be customized
- Clinic information and branding can be updated
- Security settings and password policies can be modified
- Backup and maintenance schedules can be configured
- Integration settings for external systems can be managed

**Priority**: Low  
**Story Points**: 7

---

## User Story Summary

| Epic | Total Stories | High Priority | Medium Priority | Low Priority | Total Story Points |
|------|---------------|---------------|-----------------|--------------|-------------------|
| Authentication & Authorization | 3 | 3 | 0 | 0 | 16 |
| Patient Management | 3 | 1 | 2 | 0 | 16 |
| Appointment Management | 4 | 2 | 2 | 0 | 23 |
| Medical Records | 3 | 2 | 1 | 0 | 23 |
| Prescription Management | 2 | 1 | 1 | 0 | 13 |
| Administrative Functions | 3 | 0 | 1 | 2 | 25 |
| **TOTAL** | **18** | **9** | **7** | **2** | **116** |

---

## Implementation Priority

### Phase 1 (MVP - Minimum Viable Product)
- User Authentication and Authorization
- Basic Patient Management
- Core Appointment Management
- Basic Medical Records

### Phase 2 (Enhanced Features)
- Advanced Medical Records Management
- Prescription Management
- Patient Self-Service Features

### Phase 3 (Administrative & Reporting)
- Advanced Administrative Functions
- Comprehensive Reporting
- System Configuration Tools

---

*These user stories will guide the development process through Modules 2-5 of the capstone project.*