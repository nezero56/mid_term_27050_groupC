-- =====================================================
-- CHURCH MANAGEMENT SYSTEM - SEED DATA
-- Rwanda: Province → District → Sector → Cell → Village
-- =====================================================

-- Clear existing data (correct FK order)
DELETE FROM user_roles;
DELETE FROM user_profiles;
DELETE FROM users;
DELETE FROM roles;
DELETE FROM locations WHERE location_type = 'VILLAGE';
DELETE FROM locations WHERE location_type = 'CELL';
DELETE FROM locations WHERE location_type = 'SECTOR';
DELETE FROM locations WHERE location_type = 'DISTRICT';
DELETE FROM locations WHERE location_type = 'PROVINCE';

-- Reset sequences
ALTER SEQUENCE IF EXISTS roles_id_seq RESTART WITH 1;
ALTER SEQUENCE IF EXISTS users_id_seq RESTART WITH 1;
ALTER SEQUENCE IF EXISTS user_profiles_id_seq RESTART WITH 1;

-- =====================================================
-- ROLES (Many-to-Many with User)
-- =====================================================
INSERT INTO roles (name, description) VALUES
('MEMBER',       'Regular church member'),
('LEADER',       'Church leader/elder'),
('TREASURER',    'Finance manager'),
('PASTOR',       'Spiritual leader'),
('SECRETARY',    'Administrative secretary'),
('CHOIR_LEADER', 'Choir coordinator');

-- =====================================================
-- PROVINCES (5 provinces of Rwanda)
-- =====================================================
INSERT INTO locations (id, code, name, location_type, parent_id) VALUES
('00000000-0000-0000-0000-000000000001', 'KGL',  'Kigali City',       'PROVINCE', NULL),
('00000000-0000-0000-0000-000000000002', 'SP',   'Southern Province',  'PROVINCE', NULL),
('00000000-0000-0000-0000-000000000003', 'NP',   'Northern Province',  'PROVINCE', NULL),
('00000000-0000-0000-0000-000000000004', 'EP',   'Eastern Province',   'PROVINCE', NULL),
('00000000-0000-0000-0000-000000000005', 'WP',   'Western Province',   'PROVINCE', NULL);

-- =====================================================
-- DISTRICTS
-- Kigali City: Gasabo, Kicukiro, Nyarugenge
-- Southern Province: Huye, Nyanza
-- Northern Province: Musanze, Gicumbi
-- Eastern Province: Rwamagana, Kayonza
-- Western Province: Rubavu, Rusizi
-- =====================================================
INSERT INTO locations (id, code, name, location_type, parent_id) VALUES
-- Kigali City districts
('00000000-0000-0000-0001-000000000001', 'GSB',  'Gasabo',     'DISTRICT', '00000000-0000-0000-0000-000000000001'),
('00000000-0000-0000-0001-000000000002', 'KCK',  'Kicukiro',   'DISTRICT', '00000000-0000-0000-0000-000000000001'),
('00000000-0000-0000-0001-000000000003', 'NYR',  'Nyarugenge', 'DISTRICT', '00000000-0000-0000-0000-000000000001'),
-- Southern Province districts
('00000000-0000-0000-0001-000000000004', 'HUY',  'Huye',       'DISTRICT', '00000000-0000-0000-0000-000000000002'),
('00000000-0000-0000-0001-000000000005', 'NYZ',  'Nyanza',     'DISTRICT', '00000000-0000-0000-0000-000000000002'),
-- Northern Province districts
('00000000-0000-0000-0001-000000000006', 'MSZ',  'Musanze',    'DISTRICT', '00000000-0000-0000-0000-000000000003'),
('00000000-0000-0000-0001-000000000007', 'GCB',  'Gicumbi',    'DISTRICT', '00000000-0000-0000-0000-000000000003'),
-- Eastern Province districts
('00000000-0000-0000-0001-000000000008', 'RWM',  'Rwamagana',  'DISTRICT', '00000000-0000-0000-0000-000000000004'),
('00000000-0000-0000-0001-000000000009', 'KYZ',  'Kayonza',    'DISTRICT', '00000000-0000-0000-0000-000000000004'),
-- Western Province districts
('00000000-0000-0000-0001-000000000010', 'RBV',  'Rubavu',     'DISTRICT', '00000000-0000-0000-0000-000000000005'),
('00000000-0000-0000-0001-000000000011', 'RSZ',  'Rusizi',     'DISTRICT', '00000000-0000-0000-0000-000000000005');

-- =====================================================
-- SECTORS
-- =====================================================
INSERT INTO locations (id, code, name, location_type, parent_id) VALUES
-- Gasabo sectors
('00000000-0000-0000-0002-000000000001', 'GSB-REM', 'Remera',    'SECTOR', '00000000-0000-0000-0001-000000000001'),
('00000000-0000-0000-0002-000000000002', 'GSB-KIM', 'Kimironko', 'SECTOR', '00000000-0000-0000-0001-000000000001'),
-- Kicukiro sectors
('00000000-0000-0000-0002-000000000003', 'KCK-NIA', 'Niboye',    'SECTOR', '00000000-0000-0000-0001-000000000002'),
('00000000-0000-0000-0002-000000000004', 'KCK-KAG', 'Kagarama',  'SECTOR', '00000000-0000-0000-0001-000000000002'),
-- Nyarugenge sectors
('00000000-0000-0000-0002-000000000005', 'NYR-NYM', 'Nyamirambo','SECTOR', '00000000-0000-0000-0001-000000000003'),
('00000000-0000-0000-0002-000000000006', 'NYR-KYI', 'Kiyovu',    'SECTOR', '00000000-0000-0000-0001-000000000003'),
-- Huye sectors
('00000000-0000-0000-0002-000000000007', 'HUY-NGO', 'Ngoma',     'SECTOR', '00000000-0000-0000-0001-000000000004'),
('00000000-0000-0000-0002-000000000008', 'HUY-MBR', 'Mbazi',     'SECTOR', '00000000-0000-0000-0001-000000000004'),
-- Musanze sectors
('00000000-0000-0000-0002-000000000009', 'MSZ-MUS', 'Musanze',   'SECTOR', '00000000-0000-0000-0001-000000000006'),
('00000000-0000-0000-0002-000000000010', 'MSZ-KIN', 'Kinigi',    'SECTOR', '00000000-0000-0000-0001-000000000006'),
-- Rubavu sectors
('00000000-0000-0000-0002-000000000011', 'RBV-GIS', 'Gisenyi',   'SECTOR', '00000000-0000-0000-0001-000000000010'),
('00000000-0000-0000-0002-000000000012', 'RBV-RUB', 'Rubavu',    'SECTOR', '00000000-0000-0000-0001-000000000010');

-- =====================================================
-- CELLS
-- =====================================================
INSERT INTO locations (id, code, name, location_type, parent_id) VALUES
-- Remera cells
('00000000-0000-0000-0003-000000000001', 'GSB-REM-C1', 'Bibare',    'CELL', '00000000-0000-0000-0002-000000000001'),
('00000000-0000-0000-0003-000000000002', 'GSB-REM-C2', 'Nyabisindu','CELL', '00000000-0000-0000-0002-000000000001'),
-- Kimironko cells
('00000000-0000-0000-0003-000000000003', 'GSB-KIM-C1', 'Bibare',    'CELL', '00000000-0000-0000-0002-000000000002'),
('00000000-0000-0000-0003-000000000004', 'GSB-KIM-C2', 'Kibagabaga','CELL', '00000000-0000-0000-0002-000000000002'),
-- Niboye cells
('00000000-0000-0000-0003-000000000005', 'KCK-NIA-C1', 'Gatare',    'CELL', '00000000-0000-0000-0002-000000000003'),
('00000000-0000-0000-0003-000000000006', 'KCK-NIA-C2', 'Kabuye',    'CELL', '00000000-0000-0000-0002-000000000003'),
-- Nyamirambo cells
('00000000-0000-0000-0003-000000000007', 'NYR-NYM-C1', 'Cyivugiza', 'CELL', '00000000-0000-0000-0002-000000000005'),
('00000000-0000-0000-0003-000000000008', 'NYR-NYM-C2', 'Kivugiza',  'CELL', '00000000-0000-0000-0002-000000000005'),
-- Ngoma cells
('00000000-0000-0000-0003-000000000009', 'HUY-NGO-C1', 'Cyarwa',    'CELL', '00000000-0000-0000-0002-000000000007'),
('00000000-0000-0000-0003-000000000010', 'HUY-NGO-C2', 'Kabutare',  'CELL', '00000000-0000-0000-0002-000000000007'),
-- Musanze cells
('00000000-0000-0000-0003-000000000011', 'MSZ-MUS-C1', 'Cyabararika','CELL','00000000-0000-0000-0002-000000000009'),
('00000000-0000-0000-0003-000000000012', 'MSZ-MUS-C2', 'Gacaca',    'CELL', '00000000-0000-0000-0002-000000000009'),
-- Gisenyi cells
('00000000-0000-0000-0003-000000000013', 'RBV-GIS-C1', 'Buhanga',   'CELL', '00000000-0000-0000-0002-000000000011'),
('00000000-0000-0000-0003-000000000014', 'RBV-GIS-C2', 'Kivumu',    'CELL', '00000000-0000-0000-0002-000000000011');

-- =====================================================
-- VILLAGES
-- =====================================================
INSERT INTO locations (id, code, name, location_type, parent_id) VALUES
-- Bibare (Remera) villages
('00000000-0000-0000-0004-000000000001', 'GSB-REM-C1-V1', 'Akabahizi',  'VILLAGE', '00000000-0000-0000-0003-000000000001'),
('00000000-0000-0000-0004-000000000002', 'GSB-REM-C1-V2', 'Amahoro',    'VILLAGE', '00000000-0000-0000-0003-000000000001'),
-- Nyabisindu (Remera) villages
('00000000-0000-0000-0004-000000000003', 'GSB-REM-C2-V1', 'Inzovu',     'VILLAGE', '00000000-0000-0000-0003-000000000002'),
('00000000-0000-0000-0004-000000000004', 'GSB-REM-C2-V2', 'Urugwiro',   'VILLAGE', '00000000-0000-0000-0003-000000000002'),
-- Kibagabaga (Kimironko) villages
('00000000-0000-0000-0004-000000000005', 'GSB-KIM-C2-V1', 'Kamatamu',   'VILLAGE', '00000000-0000-0000-0003-000000000004'),
('00000000-0000-0000-0004-000000000006', 'GSB-KIM-C2-V2', 'Rugarama',   'VILLAGE', '00000000-0000-0000-0003-000000000004'),
-- Gatare (Niboye) villages
('00000000-0000-0000-0004-000000000007', 'KCK-NIA-C1-V1', 'Gahanga',    'VILLAGE', '00000000-0000-0000-0003-000000000005'),
('00000000-0000-0000-0004-000000000008', 'KCK-NIA-C1-V2', 'Kanombe',    'VILLAGE', '00000000-0000-0000-0003-000000000005'),
-- Cyivugiza (Nyamirambo) villages
('00000000-0000-0000-0004-000000000009', 'NYR-NYM-C1-V1', 'Biryogo',    'VILLAGE', '00000000-0000-0000-0003-000000000007'),
('00000000-0000-0000-0004-000000000010', 'NYR-NYM-C1-V2', 'Rwezamenyo', 'VILLAGE', '00000000-0000-0000-0003-000000000007'),
-- Cyarwa (Ngoma/Huye) villages
('00000000-0000-0000-0004-000000000011', 'HUY-NGO-C1-V1', 'Cyarwa',     'VILLAGE', '00000000-0000-0000-0003-000000000009'),
('00000000-0000-0000-0004-000000000012', 'HUY-NGO-C1-V2', 'Sovu',       'VILLAGE', '00000000-0000-0000-0003-000000000009'),
-- Cyabararika (Musanze) villages
('00000000-0000-0000-0004-000000000013', 'MSZ-MUS-C1-V1', 'Cyabararika','VILLAGE', '00000000-0000-0000-0003-000000000011'),
('00000000-0000-0000-0004-000000000014', 'MSZ-MUS-C1-V2', 'Gataraga',   'VILLAGE', '00000000-0000-0000-0003-000000000011'),
-- Buhanga (Gisenyi/Rubavu) villages
('00000000-0000-0000-0004-000000000015', 'RBV-GIS-C1-V1', 'Buhanga',    'VILLAGE', '00000000-0000-0000-0003-000000000013'),
('00000000-0000-0000-0004-000000000016', 'RBV-GIS-C1-V2', 'Pfunda',     'VILLAGE', '00000000-0000-0000-0003-000000000013');

-- =====================================================
-- USERS (linked to VILLAGE level only)
-- =====================================================
INSERT INTO users (first_name, last_name, email, phone, location_id) VALUES
-- Kigali City users (Gasabo - Remera villages)
('Alice',   'Uwimana',   'alice@church.rw',   '+250788001001', '00000000-0000-0000-0004-000000000001'),
('Bob',     'Nkurunziza','bob@church.rw',      '+250788001002', '00000000-0000-0000-0004-000000000002'),
('Claire',  'Mukamana',  'claire@church.rw',   '+250788001003', '00000000-0000-0000-0004-000000000003'),
('David',   'Habimana',  'david@church.rw',    '+250788001004', '00000000-0000-0000-0004-000000000004'),
-- Kigali City users (Kicukiro - Niboye villages)
('Eve',     'Ingabire',  'eve@church.rw',      '+250788001005', '00000000-0000-0000-0004-000000000007'),
('Frank',   'Bizimana',  'frank@church.rw',    '+250788001006', '00000000-0000-0000-0004-000000000008'),
-- Kigali City users (Nyarugenge - Nyamirambo villages)
('Grace',   'Uwase',     'grace@church.rw',    '+250788001007', '00000000-0000-0000-0004-000000000009'),
('Henry',   'Niyonzima', 'henry@church.rw',    '+250788001008', '00000000-0000-0000-0004-000000000010'),
-- Southern Province users (Huye)
('Irene',   'Mukandori', 'irene@church.rw',    '+250788001009', '00000000-0000-0000-0004-000000000011'),
('James',   'Nshimiyimana','james@church.rw',  '+250788001010', '00000000-0000-0000-0004-000000000012'),
-- Northern Province users (Musanze)
('Karen',   'Umubyeyi',  'karen@church.rw',    '+250788001011', '00000000-0000-0000-0004-000000000013'),
('Louis',   'Hakizimana','louis@church.rw',    '+250788001012', '00000000-0000-0000-0004-000000000014'),
-- Western Province users (Rubavu)
('Mary',    'Nyiraneza',  'mary@church.rw',    '+250788001013', '00000000-0000-0000-0004-000000000015'),
('Nathan',  'Tuyishime',  'nathan@church.rw',  '+250788001014', '00000000-0000-0000-0004-000000000016');

-- =====================================================
-- USER PROFILES (One-to-One with User)
-- =====================================================
INSERT INTO user_profiles (gender, date_of_birth, address, occupation, emergency_contact, user_id, location_id) VALUES
('Female', '1995-03-12', 'KG 123 St, Remera',     'Teacher',    '+250788002001', 1,  '00000000-0000-0000-0004-000000000001'),
('Male',   '1990-07-25', 'KG 456 St, Remera',     'Engineer',   '+250788002002', 2,  '00000000-0000-0000-0004-000000000002'),
('Female', '1998-11-05', 'KG 789 St, Remera',     'Nurse',      '+250788002003', 3,  '00000000-0000-0000-0004-000000000003'),
('Male',   '1985-01-18', 'KG 321 St, Remera',     'Accountant', '+250788002004', 4,  '00000000-0000-0000-0004-000000000004'),
('Female', '1993-06-30', 'KK 100 St, Niboye',     'Doctor',     '+250788002005', 5,  '00000000-0000-0000-0004-000000000007'),
('Male',   '1988-09-14', 'KK 200 St, Niboye',     'Lawyer',     '+250788002006', 6,  '00000000-0000-0000-0004-000000000008'),
('Female', '1997-04-22', 'KN 50 St, Nyamirambo',  'Designer',   '+250788002007', 7,  '00000000-0000-0000-0004-000000000009'),
('Male',   '1992-12-08', 'KN 75 St, Nyamirambo',  'Farmer',     '+250788002008', 8,  '00000000-0000-0000-0004-000000000010'),
('Female', '1996-02-17', 'Huye Town, Cyarwa',      'Student',    '+250788002009', 9,  '00000000-0000-0000-0004-000000000011'),
('Male',   '1983-08-03', 'Huye Town, Sovu',        'Pastor',     '+250788002010', 10, '00000000-0000-0000-0004-000000000012'),
('Female', '1994-05-27', 'Musanze Town',           'Trader',     '+250788002011', 11, '00000000-0000-0000-0004-000000000013'),
('Male',   '1989-10-11', 'Musanze Town',           'Driver',     '+250788002012', 12, '00000000-0000-0000-0004-000000000014'),
('Female', '1999-07-19', 'Gisenyi, Buhanga',       'Secretary',  '+250788002013', 13, '00000000-0000-0000-0004-000000000015'),
('Male',   '1991-03-06', 'Gisenyi, Pfunda',        'Mechanic',   '+250788002014', 14, '00000000-0000-0000-0004-000000000016');

-- =====================================================
-- USER ROLES (Many-to-Many join table)
-- =====================================================
INSERT INTO user_roles (user_id, role_id) VALUES
(1,  1), -- Alice   → MEMBER
(1,  3), -- Alice   → TREASURER
(2,  1), -- Bob     → MEMBER
(2,  2), -- Bob     → LEADER
(3,  1), -- Claire  → MEMBER
(4,  4), -- David   → PASTOR
(4,  2), -- David   → LEADER
(5,  1), -- Eve     → MEMBER
(5,  6), -- Eve     → CHOIR_LEADER
(6,  1), -- Frank   → MEMBER
(7,  1), -- Grace   → MEMBER
(7,  5), -- Grace   → SECRETARY
(8,  1), -- Henry   → MEMBER
(9,  1), -- Irene   → MEMBER
(10, 4), -- James   → PASTOR
(11, 1), -- Karen   → MEMBER
(11, 6), -- Karen   → CHOIR_LEADER
(12, 2), -- Louis   → LEADER
(13, 1), -- Mary    → MEMBER
(14, 1); -- Nathan  → MEMBER
