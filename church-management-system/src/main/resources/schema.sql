-- =====================================================
-- CHURCH MANAGEMENT SYSTEM - DATABASE SCHEMA
-- Self-Referenced Location Structure
-- =====================================================

-- Drop tables if they exist (in correct order due to foreign keys)
DROP TABLE IF EXISTS user_roles;
DROP TABLE IF EXISTS user_profiles;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS roles;
DROP TABLE IF EXISTS locations;

-- =====================================================
-- LOCATIONS TABLE (Self-Referenced)
-- Province → District → Sector → Cell → Village
-- =====================================================
CREATE TABLE locations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(255) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    location_type VARCHAR(50) NOT NULL,
    parent_id BIGINT,
    CONSTRAINT fk_location_parent FOREIGN KEY (parent_id) REFERENCES locations(id) ON DELETE CASCADE,
    CONSTRAINT chk_location_type CHECK (location_type IN ('PROVINCE', 'DISTRICT', 'SECTOR', 'CELL', 'VILLAGE'))
);

-- Create indexes for better query performance
CREATE INDEX idx_location_code ON locations(code);
CREATE INDEX idx_location_type ON locations(location_type);
CREATE INDEX idx_location_parent ON locations(parent_id);

-- =====================================================
-- ROLES TABLE (For Many-to-Many relationship)
-- =====================================================
CREATE TABLE roles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(255)
);

-- =====================================================
-- USERS TABLE
-- =====================================================
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    phone VARCHAR(20),
    location_id BIGINT NOT NULL,
    CONSTRAINT fk_user_location FOREIGN KEY (location_id) REFERENCES locations(id) ON DELETE RESTRICT
);

-- Create indexes
CREATE INDEX idx_user_email ON users(email);
CREATE INDEX idx_user_location ON users(location_id);

-- =====================================================
-- USER_PROFILES TABLE (One-to-One with Users)
-- =====================================================
CREATE TABLE user_profiles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    gender VARCHAR(20),
    date_of_birth VARCHAR(50),
    address VARCHAR(255),
    occupation VARCHAR(100),
    emergency_contact VARCHAR(100),
    location_id BIGINT,
    user_id BIGINT NOT NULL UNIQUE,
    CONSTRAINT fk_profile_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_profile_location FOREIGN KEY (location_id) REFERENCES locations(id) ON DELETE SET NULL
);

-- Create index
CREATE INDEX idx_profile_user ON user_profiles(user_id);
CREATE INDEX idx_profile_location ON user_profiles(location_id);

-- =====================================================
-- USER_ROLES TABLE (Many-to-Many Join Table)
-- =====================================================
CREATE TABLE user_roles (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    CONSTRAINT fk_user_roles_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_user_roles_role FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
);

-- Create indexes
CREATE INDEX idx_user_roles_user ON user_roles(user_id);
CREATE INDEX idx_user_roles_role ON user_roles(role_id);

-- =====================================================
-- COMMENTS AND DOCUMENTATION
-- =====================================================

-- LOCATIONS TABLE EXPLANATION:
-- This table uses a self-referencing design where:
-- - Provinces have parent_id = NULL
-- - Districts have parent_id = Province.id
-- - Sectors have parent_id = District.id
-- - Cells have parent_id = Sector.id
-- - Villages have parent_id = Cell.id
--
-- This allows for flexible hierarchy navigation and easy querying
-- from any level to any other level in the administrative structure.

-- USERS TABLE EXPLANATION:
-- Users are linked to a location (typically a Village).
-- Through the self-referenced locations table, we can:
-- - Find all users in a Province by traversing: User → Village → Cell → Sector → District → Province
-- - Find all users in any administrative level using JOIN queries

-- USER_PROFILES TABLE EXPLANATION:
-- One-to-One relationship with Users table.
-- Each user has exactly one profile.
-- The user_id column is UNIQUE to enforce the one-to-one relationship.

-- USER_ROLES TABLE EXPLANATION:
-- Many-to-Many relationship between Users and Roles.
-- A user can have multiple roles (e.g., MEMBER, LEADER, TREASURER).
-- A role can be assigned to multiple users.

-- =====================================================
-- END OF SCHEMA
-- =====================================================
