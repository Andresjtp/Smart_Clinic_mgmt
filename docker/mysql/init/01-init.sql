-- Smart Clinic Management System Database Initialization
-- This script sets up the database with initial data

-- Grant privileges to the clinic_user
GRANT ALL PRIVILEGES ON smart_clinic_db.* TO 'clinic_user'@'%';
FLUSH PRIVILEGES;

USE smart_clinic_db;

-- Note: Tables will be created automatically by Hibernate when the application starts
-- This file can contain initial data insertions if needed

-- Example: Insert default admin user (this would be handled by the application's data.sql)
-- The application will handle schema creation and initial data loading