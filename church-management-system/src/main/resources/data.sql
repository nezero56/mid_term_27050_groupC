-- =====================================================
-- RWANDA ADMINISTRATIVE STRUCTURE DATA
-- Self-Referenced Location Table
-- Province → District → Sector → Cell → Village
-- =====================================================

-- Clear existing data (in correct order due to foreign keys)
DELETE FROM user_roles;
DELETE FROM user_profiles;
DELETE FROM users;
DELETE FROM roles;

-- Reset sequences (PostgreSQL)
ALTER SEQUENCE IF EXISTS roles_id_seq RESTART WITH 1;

-- =====================================================
-- ROLES (For Many-to-Many relationship demo)
-- =====================================================
INSERT INTO roles (name, description) VALUES
('MEMBER', 'Regular church member'),
('LEADER', 'Church leader/elder'),
('TREASURER', 'Finance manager'),
('PASTOR', 'Spiritual leader'),
('SECRETARY', 'Administrative secretary'),
('CHOIR_LEADER', 'Choir coordinator');

-- =====================================================
-- LOCATIONS
-- =====================================================
-- Locations will be inserted via Postman API
-- No automatic data loading for locations
-- Use POST /api/locations to create locations manually
-- 
-- Example codes:
-- Province: "KGL" for Kigali, "SP" for Southern Province
-- District: "KCK" for Kicukiro, "GSB" for Gasabo
-- Sector: "KCK-S1" for Kicukiro Sector 1
-- Cell: "KCK-S1-C1" for Kicukiro Sector 1 Cell 1
-- Village: "KCK-S1-C1-V1" for Kicukiro Sector 1 Cell 1 Village 1
