# Church Management System - Setup Guide

## ✅ Automatic Table Creation

This project is configured to **automatically create all database tables** when you run the application!

---

## How It Works

### 1. **Hibernate Auto-DDL**
The application uses Hibernate to automatically create tables based on your JPA entities.

**Configuration in `application.properties`:**
```properties
spring.jpa.hibernate.ddl-auto=update
```

**Options:**
- `update` - Creates tables if they don't exist, updates schema if entities change (RECOMMENDED for development)
- `create` - Drops and recreates tables every time (loses data)
- `create-drop` - Creates tables on startup, drops on shutdown
- `validate` - Only validates schema, doesn't create tables
- `none` - No automatic schema management

### 2. **Automatic Data Loading**
After tables are created, the application automatically loads sample data from `data.sql`.

**Configuration in `application.properties`:**
```properties
spring.sql.init.mode=always
spring.jpa.defer-datasource-initialization=true
spring.sql.init.data-locations=classpath:data.sql
```

---

## Database Setup

### Prerequisites
1. **PostgreSQL** must be installed and running
2. Create a database named `church_db`

```sql
CREATE DATABASE church_db;
```

### Database Configuration
Update `application.properties` if needed:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/church_db
spring.datasource.username=postgres
spring.datasource.password=27050
```

---

## Running the Application

### Option 1: Using Maven
```bash
mvn spring-boot:run
```

### Option 2: Using IDE
Run the main class: `ChurchManagementApplication.java`

### Option 3: Using JAR
```bash
mvn clean package
java -jar target/church-management-system-1.0.0.jar
```

---

## What Happens on Startup

### Step 1: Table Creation
Hibernate automatically creates these tables:
- ✅ `locations` (self-referenced: Province → District → Sector → Cell → Village)
- ✅ `roles` (for Many-to-Many relationship)
- ✅ `users` (linked to locations)
- ✅ `user_profiles` (One-to-One with users)
- ✅ `user_roles` (Many-to-Many join table)

### Step 2: Data Loading
The `data.sql` file automatically populates:
- ✅ 5 Provinces
- ✅ 30 Districts
- ✅ 35 Sectors (sample)
- ✅ 13 Cells (sample)
- ✅ 20 Villages (sample)
- ✅ 6 Roles

### Step 3: Application Ready
```
Started ChurchManagementApplication in X.XXX seconds
```

---

## Tables Created

### 1. locations (Self-Referenced)
```sql
CREATE TABLE locations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(255) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    location_type VARCHAR(50) NOT NULL,
    parent_id BIGINT REFERENCES locations(id)
);
```

### 2. roles
```sql
CREATE TABLE roles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(255)
);
```

### 3. users
```sql
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    phone VARCHAR(20),
    location_id BIGINT NOT NULL REFERENCES locations(id)
);
```

### 4. user_profiles
```sql
CREATE TABLE user_profiles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    gender VARCHAR(20),
    date_of_birth VARCHAR(50),
    address VARCHAR(255),
    occupation VARCHAR(100),
    emergency_contact VARCHAR(100),
    location_id BIGINT REFERENCES locations(id),
    user_id BIGINT NOT NULL UNIQUE REFERENCES users(id)
);
```

### 5. user_roles (Join Table)
```sql
CREATE TABLE user_roles (
    user_id BIGINT NOT NULL REFERENCES users(id),
    role_id BIGINT NOT NULL REFERENCES roles(id),
    PRIMARY KEY (user_id, role_id)
);
```

---

## Verifying Tables

### Using PostgreSQL Command Line
```bash
psql -U postgres -d church_db
```

```sql
-- List all tables
\dt

-- Check locations table
SELECT * FROM locations WHERE location_type = 'PROVINCE';

-- Check roles
SELECT * FROM roles;

-- Count records
SELECT location_type, COUNT(*) FROM locations GROUP BY location_type;
```

### Expected Output
```
 location_type | count 
---------------+-------
 PROVINCE      |     5
 DISTRICT      |    30
 SECTOR        |    35
 CELL          |    13
 VILLAGE       |    20
```

---

## Testing the API

### 1. Get All Provinces
```bash
curl http://localhost:8080/api/locations/provinces
```

### 2. Get All Locations
```bash
curl http://localhost:8080/api/locations
```

### 3. Get Location by Code
```bash
curl http://localhost:8080/api/locations/code/01
```

### 4. Get Children of a Location
```bash
# Get districts of Kigali (assuming Kigali has id=1)
curl http://localhost:8080/api/locations/1/children
```

### 5. Get All Roles
```bash
curl http://localhost:8080/api/roles
```

---

## Troubleshooting

### Issue: Tables Not Created
**Solution:** Check that:
1. PostgreSQL is running
2. Database `church_db` exists
3. Connection credentials are correct in `application.properties`

### Issue: Data Not Loaded
**Solution:** Check:
1. `spring.sql.init.mode=always` is set
2. `spring.jpa.defer-datasource-initialization=true` is set
3. Check application logs for SQL errors

### Issue: Duplicate Key Errors
**Solution:** 
1. Drop and recreate the database:
```sql
DROP DATABASE church_db;
CREATE DATABASE church_db;
```
2. Restart the application

### Issue: Foreign Key Constraint Errors
**Solution:** The `data.sql` uses SELECT subqueries to reference parent IDs. Make sure:
1. Parent records are inserted before children
2. Codes are unique and correct

---

## Development vs Production

### Development (Current Setup)
```properties
spring.jpa.hibernate.ddl-auto=update
spring.sql.init.mode=always
```
- Tables auto-created
- Data auto-loaded
- Schema updates automatically

### Production (Recommended)
```properties
spring.jpa.hibernate.ddl-auto=validate
spring.sql.init.mode=never
```
- Use migration tools (Flyway/Liquibase)
- Manual schema management
- No automatic data loading

---

## Files Overview

### Configuration
- `application.properties` - Database and JPA configuration
- `pom.xml` - Maven dependencies

### SQL Files
- `schema.sql` - Manual table creation (optional, not used with auto-DDL)
- `data.sql` - Sample data (automatically loaded)

### Entities
- `Location.java` - Self-referenced location entity
- `User.java` - User entity
- `UserProfile.java` - User profile entity
- `Role.java` - Role entity

---

## Summary

✅ **No manual SQL execution needed!**
✅ **Just run the application and everything is set up automatically!**
✅ **Tables are created from JPA entities**
✅ **Sample data is loaded from data.sql**

---

## Author
Sonia Munezero

## Date
March 13, 2026
