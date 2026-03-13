Church Management System
A Spring Boot REST API with Rwanda Administrative Location Hierarchy
Author: Sonia Munezero
Institution: Adventist University of Central Africa (AUCA)
Version: 1.0.0

Overview
The Church Management System is a RESTful backend application built with Spring Boot and PostgreSQL. It manages church members, user profiles, roles, and Rwanda's full administrative location hierarchy (Province → District → Sector → Cell → Village) using a self-referencing entity model.

Technology Stack
TechnologyVersionPurposeJava17Programming languageSpring Boot3.2.0Backend frameworkSpring Data JPA3.2.0Database ORMPostgreSQL18Relational databaseHibernate6.3.1JPA implementationLombokLatestBoilerplate reductionMaven3.xBuild tool

Project Structure

church-management-system/
├── src/
│   └── main/
│       ├── java/
│       │   └── rw/
│       │       └── churchmanagement/
│       │           ├── ChurchManagementApplication.java   ← Main entry point
│       │           ├── controller/
│       │           │   ├── LocationController.java
│       │           │   └── UserController.java
│       │           ├── model/
│       │           │   ├── Location.java                  ← Self-referencing entity
│       │           │   ├── User.java
│       │           │   ├── UserProfile.java
│       │           │   └── Role.java
│       │           ├── repository/
│       │           │   ├── LocationRepository.java
│       │           │   ├── UserRepository.java
│       │           │   └── RoleRepository.java
│       │           ├── service/
│       │           │   ├── LocationService.java
│       │           │   └── UserService.java
│       │           └── dto/
│       │               └── LocationDTO.java
│       └── resources/
│           ├── application.properties
│           └── data.sql
├── pom.xml
└── README.md

Database Configuration
The application uses PostgreSQL. Create the database before running:
sqlCREATE DATABASE church_db;
Update src/main/resources/application.properties with your credentials:
propertiesspring.datasource.url=jdbc:postgresql://localhost:5432/church_db
spring.datasource.username=postgres
spring.datasource.password=your_password
spring.jpa.hibernate.ddl-auto=update

How to Run
Prerequisites

Java 17 installed
PostgreSQL running locally on port 5432
Maven installed

Steps
1. Clone or open the project in VS Code
2. Create the database in pgAdmin or psql:
sqlCREATE DATABASE church_db;
3. Run the application:
powershellmvn spring-boot:run
4. The server starts at:
http://localhost:8080

Location Hierarchy
The system models Rwanda's full 5-level administrative structure using a self-referencing Location entity:
Province
  └── District
        └── Sector
              └── Cell
                    └── Village
Each location references its parent via a parent_id UUID foreign key.
Rwanda Provinces Supported

Kigali (City)
Eastern Province
Western Province
Northern Province
Southern Province


API Endpoints
Location Endpoints — /api/locations
MethodURLDescriptionPOST/api/locationsCreate a new locationGET/api/locationsGet all locationsGET/api/locations/{id}Get location by UUIDGET/api/locations/code/{code}Get location by codeGET/api/locations/provincesGet all provincesGET/api/locations/type/{type}Get locations by typeGET/api/locations/{parentId}/childrenGet children of a locationGET/api/locations/{parentId}/children/type/{type}Get children by typePOST/api/locations/with-parent?parentId={uuid}Create location with parent
Location Types (Enum)
PROVINCE, DISTRICT, SECTOR, CELL, VILLAGE

Sample API Usage (Postman)
Create a Province
jsonPOST http://localhost:8080/api/locations

{
  "code": "KGL",
  "name": "Kigali",
  "locationType": "PROVINCE",
  "parentId": null
}
Create a District (with parent Province)
jsonPOST http://localhost:8080/api/locations

{
  "code": "NYA",
  "name": "Nyarugenge",
  "locationType": "DISTRICT",
  "parentId": "YOUR_PROVINCE_UUID_HERE"
}
Create a Sector
jsonPOST http://localhost:8080/api/locations

{
  "code": "NYA-GIT",
  "name": "Gitega",
  "locationType": "SECTOR",
  "parentId": "YOUR_DISTRICT_UUID_HERE"
}
Create a Cell
jsonPOST http://localhost:8080/api/locations

{
  "code": "GIT-AGA",
  "name": "Agakiriro",
  "locationType": "CELL",
  "parentId": "YOUR_SECTOR_UUID_HERE"
}
Create a Village
jsonPOST http://localhost:8080/api/locations

{
  "code": "AGA-VIL1",
  "name": "Cyivugiza",
  "locationType": "VILLAGE",
  "parentId": "YOUR_CELL_UUID_HERE"
}

Key Design Decisions
UUID Primary Keys
All entities use UUID instead of auto-increment integers to ensure uniqueness across distributed systems and avoid ID conflicts when merging data.
Self-Referencing Location Entity
A single locations table handles all 5 levels of Rwanda's administrative hierarchy. Each record has an optional parent_id that references another record in the same table. This avoids having 5 separate tables and makes queries flexible.
DTO Pattern
The LocationDTO class separates the API request format from the internal entity model. The parentId field in the DTO is a plain UUID string, while the entity uses a full Location object for the parent — the service layer handles the conversion by looking up the parent from the database.

Common Issues & Fixes
ProblemCauseFixUnable to find main classWrong package in pom.xmlSet <mainClass>rw.churchmanagement.ChurchManagementApplication</mainClass>column cannot be cast to uuidOld table with wrong column typeDrop tables in pgAdmin and restart appPort 8080 already in usePrevious instance still runningRun taskkill /PID <pid> /Fparent_id is nullSending wrong UUID (old tables dropped)Query fresh UUID from pgAdmin before POSTing

Author Notes
This project was developed as a midterm assessment for the Web Programming course at AUCA. It demonstrates JPA entity relationships, RESTful API design, DTO patterns, and PostgreSQL integration with Spring Boot.
