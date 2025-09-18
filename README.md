# Smart Clinic Management System
## Java Development Capstone Project

[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.1.0-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-blue.svg)](https://www.mysql.com/)
[![MongoDB](https://img.shields.io/badge/MongoDB-6.0-green.svg)](https://www.mongodb.com/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

A comprehensive clinic management system designed to modernize healthcare operations by replacing manual processes with automated digital solutions. Built as part of the IBM Coursera Full Stack Developer Course Java Development Capstone Project.

## 📋 Project Overview

SmartCare Solutions is transforming outpatient clinic operations with modern software. This system empowers doctors and patients to manage schedules and health information seamlessly, providing a strong competitive edge in the healthcare tech space.

### 🎯 Business Goals
- Replace spreadsheet-based appointment management
- Digitize patient record management  
- Streamline administrative tasks
- Improve patient care through better information access
- Ensure data security and HIPAA compliance

### 👥 User Roles
- **Administrators**: System management and configuration
- **Doctors**: Patient care, appointments, and medical records
- **Patients**: Personal health management and appointment booking

## 🏗️ System Architecture

### Technology Stack
- **Backend**: Java 17 + Spring Boot 3.1
- **Frontend**: HTML5, CSS3, JavaScript (ES6+)
- **Databases**: MySQL 8.0 (structured data) + MongoDB 6.0 (flexible data)
- **Security**: JWT authentication, BCrypt encryption
- **Build Tool**: Maven
- **Deployment**: Docker + Docker Compose
- **CI/CD**: GitHub Actions

### Architecture Pattern
```
Frontend Tier (HTML/CSS/JS) 
           ↓
Backend Tier (Spring Boot)
           ↓  
Data Tier (MySQL + MongoDB)
```

## 📁 Project Structure

```
smart-clinic-management-system/
├── docs/                           # Project documentation
│   ├── module1/                    # Module 1: Requirements & Architecture
│   │   ├── README.md
│   │   ├── architecture-design.md
│   │   └── user-stories.md
│   ├── module2/                    # Module 2: Database Design (Future)
│   ├── module3/                    # Module 3: Backend Development (Future)
│   ├── module4/                    # Module 4: Frontend Development (Future)
│   ├── module5/                    # Module 5: Testing & Deployment (Future)
│   └── module6/                    # Module 6: Final Submission (Future)
├── src/
│   ├── main/
│   │   ├── java/com/smartcare/clinicmanagementsystem/
│   │   │   ├── ClinicManagementSystemApplication.java
│   │   │   ├── controller/         # REST Controllers (Future)
│   │   │   ├── service/           # Business Logic (Future)
│   │   │   ├── repository/        # Data Access (Future)
│   │   │   ├── model/             # Entity Classes (Future)
│   │   │   ├── config/            # Configuration Classes (Future)
│   │   │   └── security/          # Security Components (Future)
│   │   └── resources/
│   │       ├── application.properties
│   │       ├── static/            # Frontend Assets (Future)
│   │       └── templates/         # HTML Templates (Future)
│   └── test/                      # Unit & Integration Tests (Future)
├── docker/                        # Docker configurations (Future)
├── .github/
│   ├── workflows/                 # CI/CD Pipeline (Future)
│   └── copilot-instructions.md
├── pom.xml                        # Maven dependencies
└── README.md
```

## 🎯 Capstone Modules Progress

### ✅ Module 1: Project Overview and Requirements Gathering
- [x] **Architecture Design Document** - [View Document](./docs/module1/architecture-design.md)
- [x] **User Stories** - [View Document](./docs/module1/user-stories.md)
- **Status**: 🟢 **COMPLETED**

### 🔄 Module 2: Database Design and Implementation
- [ ] **Entity Relationship Diagrams**
- [ ] **MySQL Database Schema**
- [ ] **MongoDB Document Structure**
- [ ] **Database Migration Scripts**
- **Status**: 🔴 **PENDING**

### 🔄 Module 3: Backend API Development
- [ ] **RESTful API Endpoints**
- [ ] **Authentication & Authorization**
- [ ] **Business Logic Implementation**
- [ ] **API Documentation (Swagger)**
- **Status**: 🔴 **PENDING**

### 🔄 Module 4: Frontend Development
- [ ] **Responsive Web Interface**
- [ ] **User Role-Based Views**
- [ ] **API Integration**
- [ ] **Client-Side Validation**
- **Status**: 🔴 **PENDING**

### 🔄 Module 5: Testing and Deployment
- [ ] **Unit Testing**
- [ ] **Integration Testing**
- [ ] **Docker Configuration**
- [ ] **CI/CD Pipeline**
- **Status**: 🔴 **PENDING**

### 🔄 Module 6: Final Submission and Documentation
- [ ] **Complete Documentation**
- [ ] **Demo Video/Presentation**
- [ ] **Code Review & Refactoring**
- [ ] **Final Submission Package**
- **Status**: 🔴 **PENDING**

## 🚀 Getting Started

### Prerequisites
- Java 17 or higher
- Maven 3.6+
- MySQL 8.0+
- MongoDB 6.0+
- Docker (for deployment)
- VS Code with Java Extensions

### Quick Setup
1. **Clone the repository**
   ```bash
   git clone <your-repo-url>
   cd smart-clinic-management-system
   ```

2. **Install dependencies**
   ```bash
   mvn clean install
   ```

3. **Database Setup**
   ```bash
   # MySQL
   mysql -u root -p
   CREATE DATABASE clinic_management;
   
   # MongoDB (ensure MongoDB is running)
   # Default connection: mongodb://localhost:27017/clinic_management_nosql
   ```

4. **Run the application**
   ```bash
   mvn spring-boot:run
   ```

5. **Access the application**
   - Application: http://localhost:8080
   - API Documentation: http://localhost:8080/swagger-ui.html

## 📖 Documentation Links

### Module 1 - Requirements & Architecture
- [Module 1 Overview](./docs/module1/README.md)
- [Architecture Design Document](./docs/module1/architecture-design.md)
- [User Stories](./docs/module1/user-stories.md)

### Future Modules
- Module 2: Database Design *(Coming Soon)*
- Module 3: Backend Development *(Coming Soon)*
- Module 4: Frontend Development *(Coming Soon)*
- Module 5: Testing & Deployment *(Coming Soon)*
- Module 6: Final Submission *(Coming Soon)*

## 🔧 Development Guidelines

### Code Standards
- Follow Java naming conventions
- Use Spring Boot best practices
- Implement proper error handling
- Write comprehensive tests
- Document all public APIs

### Security Requirements
- JWT-based authentication
- Role-based access control
- Input validation and sanitization
- HTTPS encryption
- Password security (BCrypt)

### Database Design Principles
- Normalized MySQL schema for structured data
- MongoDB for flexible/unstructured data
- Proper indexing for performance
- Data validation and constraints

## 🤝 Contributing

This is a capstone project for educational purposes. The development follows the structured module approach as outlined in the IBM Coursera Full Stack Developer Course.

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 📞 Contact

**Project**: Java Development Capstone - Smart Clinic Management System  
**Course**: IBM Coursera Full Stack Developer Program  
**Date**: September 2025

---

**Note**: This README will be updated as we progress through each module of the capstone project. Each module's completion will be reflected in the progress tracking section above.