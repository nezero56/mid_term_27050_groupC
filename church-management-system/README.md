# Church Management System
**A Spring Boot REST API with Rwanda Administrative Location Hierarchy**

- **Author:** Sonia Munezero
- **Institution:** Adventist University of Central Africa (AUCA)
- **Course:** Web Programming — Midterm Assessment
- **Version:** 1.0.0

---

## Technology Stack

| Technology     | Version | Purpose              |
|----------------|---------|----------------------|
| Java           | 21      | Programming language |
| Spring Boot    | 3.2.0   | Backend framework    |
| Spring Data JPA| 3.2.0   | Database ORM         |
| PostgreSQL     | 16      | Relational database  |
| Hibernate      | 6.3.1   | JPA implementation   |
| Lombok         | Latest  | Boilerplate reduction|
| Maven          | 3.9.6   | Build tool           |

---

## Project Structure

```
church-management-system/
├── src/main/java/rw/churchmanagement/
│   ├── ChurchManagementApplication.java
│   ├── controller/
│   │   ├── LocationController.java
│   │   ├── UserController.java
│   │   ├── UserProfileController.java
│   │   ├── RoleController.java
│   │   ├── ChurchController.java
│   │   └── MemberController.java
│   ├── model/
│   │   ├── Location.java       ← Self-referencing (Province→Village)
│   │   ├── User.java           ← Many-to-Many with Role, One-to-One with UserProfile
│   │   ├── UserProfile.java    ← One-to-One with User
│   │   ├── Role.java           ← Many-to-Many with User
│   │   ├── Church.java         ← One-to-One with Location, One-to-Many with Member
│   │   └── Member.java         ← Many-to-One with Church and User
│   ├── repository/
│   │   ├── LocationRepository.java
│   │   ├── UserRepository.java
│   │   ├── UserProfileRepository.java
│   │   ├── RoleRepository.java
│   │   ├── ChurchRepository.java
│   │   └── MemberRepository.java
│   ├── service/
│   │   ├── LocationService.java
│   │   ├── UserService.java
│   │   └── ChurchService.java
│   └── dto/
│       └── LocationDTO.java
└── src/main/resources/
    ├── application.properties
    └── data.sql
```

---

## ERD — 5 Tables (3 Marks)

```
locations ──────────────────────────────────────────────────────┐
  id (UUID PK)                                                   │
  code, name, location_type, parent_id (FK → locations.id)      │
                                                                 │
users ──────────────────────────────────────────────────────────┤
  id (PK)                                                        │
  first_name, last_name, email, phone                           │
  location_id (FK → locations.id) ← Village level only          │
                                                                 │
roles                          user_roles (join table)           │
  id (PK)                        user_id (FK → users.id)        │
  name, description              role_id (FK → roles.id)        │
                                                                 │
user_profiles                                                    │
  id (PK)                                                        │
  gender, date_of_birth, address, occupation, emergency_contact │
  user_id (FK → users.id)  ← One-to-One                        │
  location_id (FK → locations.id)                               │
                                                                 │
churches                                                         │
  id (PK)                                                        │
  name, denomination, phone_number, email, founded_year         │
  location_id (FK → locations.id) ← One-to-One                 │
                                                                 │
members                                                          │
  id (PK)                                                        │
  membership_number, joined_date, status                        │
  church_id (FK → churches.id) ← Many-to-One                   │
  user_id   (FK → users.id)    ← Many-to-One                   │
```

**Tables:** `locations`, `users`, `roles`, `user_profiles`, `churches`, `members`, `user_roles` (join table)

---

## How to Run

### Prerequisites
- Java 21
- PostgreSQL 16 running on port 5432
- Maven (installed at `C:\Users\HP\maven\apache-maven-3.9.6`)

### Step 1 — Load Maven into current session
```powershell
$env:PATH += ";C:\Users\HP\maven\apache-maven-3.9.6\bin"
```

### Step 2 — Create the database (first time only)
```powershell
$env:PGPASSWORD="27050"
& "C:\Program Files\PostgreSQL\16\bin\psql.exe" -U postgres -c "CREATE DATABASE church_db;"
```

### Step 3 — Run the application
```powershell
cd C:\Users\HP\mid_term_27050_groupC\church-management-system
mvn spring-boot:run
```

### Step 4 — Verify
```
Tomcat started on port 8082
```
App is available at: **http://localhost:8082**

---

## Database Seed Data (auto-loaded via data.sql)

| Table         | Records |
|---------------|---------|
| locations     | 58 (5 provinces, 11 districts, 12 sectors, 14 cells, 16 villages) |
| users         | 14      |
| user_profiles | 14      |
| roles         | 6       |
| user_roles    | 20      |

---

## Assessment Criteria — Implementation Map

### 1. ERD with 5 Tables ✅ (3 Marks)
Five entity tables: `locations`, `users`, `roles`, `user_profiles`, `churches` + `members`
All connected via foreign keys as shown in the ERD above.

---

### 2. Location Saving — Province to Village ✅ (2 Marks)
Location is saved top-down using `POST /api/locations` with a `parentId`:
```
Province → District → Sector → Cell → Village
```
Users are linked to **Village level only**. The hierarchy is navigated automatically via `parent_id`.

```json
POST /api/locations
{
  "code": "KGL",
  "name": "Kigali City",
  "locationType": "PROVINCE",
  "parentId": null
}
```

---

### 3. Sorting and Pagination ✅ (5 Marks)

**Pagination** loads data in pages instead of all at once — improves performance and reduces memory usage.
**Sorting** orders results by any field in ASC or DESC direction.

---

**STEP 1 — Repository layer (UserRepository.java):**
```java
// Pageable carries page number, page size AND sort direction together
Page<User> findAll(Pageable pageable);

// Sort only — no pagination
List<User> findAll(Sort sort);

// Pageable also works on custom queries
Page<User> findByProvinceCode(@Param("provinceCode") String provinceCode, Pageable pageable);
```

**MemberRepository.java — Pageable on filtered queries:**
```java
// Find members of a church — paginated
Page<Member> findByChurchId(Long churchId, Pageable pageable);

// Find members by status — paginated + sortable
Page<Member> findByStatus(String status, Pageable pageable);
```

---

**STEP 2 — Service layer (UserService.java):**
```java
// Pagination + Sorting combined
public Page<User> getAllUsers(int page, int size, String sortBy, String sortDir) {
    // Build Sort object — ASC or DESC based on sortDir parameter
    Sort sort = sortDir.equalsIgnoreCase("DESC")
        ? Sort.by(sortBy).descending()
        : Sort.by(sortBy).ascending();

    // PageRequest.of() creates a Pageable with page number, size and sort
    Pageable pageable = PageRequest.of(page, size, sort);

    // Pass Pageable to repository — Spring generates LIMIT/OFFSET SQL automatically
    return userRepository.findAll(pageable);
}

// Sorting only — no pagination
public List<User> getAllUsersSorted(String sortBy, String sortDir) {
    Sort sort = sortDir.equalsIgnoreCase("DESC")
        ? Sort.by(sortBy).descending()
        : Sort.by(sortBy).ascending();
    return userRepository.findAll(sort);
}
```

**ChurchService.java — same pattern for churches:**
```java
public Page<Church> getAllChurches(int page, int size, String sortBy, String sortDir) {
    Sort sort = sortDir.equalsIgnoreCase("DESC")
        ? Sort.by(sortBy).descending()
        : Sort.by(sortBy).ascending();
    Pageable pageable = PageRequest.of(page, size, sort);
    return churchRepository.findAll(pageable);
}
```

---

**STEP 3 — Controller layer (UserController.java):**
```java
@GetMapping
public ResponseEntity<Map<String, Object>> getAllUsers(
        @RequestParam(defaultValue = "0")         int page,     // page number (0-indexed)
        @RequestParam(defaultValue = "10")        int size,     // records per page
        @RequestParam(defaultValue = "firstName") String sortBy,  // field to sort by
        @RequestParam(defaultValue = "ASC")       String sortDir) { // ASC or DESC

    Page<User> userPage = userService.getAllUsers(page, size, sortBy, sortDir);

    Map<String, Object> response = new HashMap<>();
    response.put("users",       userPage.getContent());       // actual data
    response.put("currentPage", userPage.getNumber());        // current page index
    response.put("totalItems",  userPage.getTotalElements()); // total records in DB
    response.put("totalPages",  userPage.getTotalPages());    // total pages available
    response.put("pageSize",    userPage.getSize());          // records per page

    return ResponseEntity.ok(response);
}
```

---

**Test endpoints in Postman:**
```
# Page 1, 10 per page, sorted by firstName A→Z
GET /api/users?page=0&size=10&sortBy=firstName&sortDir=ASC

# Page 2, 5 per page, sorted by lastName Z→A
GET /api/users?page=1&size=5&sortBy=lastName&sortDir=DESC

# Sort only (no pagination)
GET /api/users/sorted?sortBy=email&sortDir=ASC

# Churches paginated
GET /api/churches?page=0&size=5&sortBy=name&sortDir=ASC
```

**Sample response:**
```json
{
  "users": [
    { "id": 1, "firstName": "Alice", "lastName": "Uwimana" },
    { "id": 3, "firstName": "Claire", "lastName": "Mukamana" }
  ],
  "currentPage": 0,
  "totalItems": 14,
  "totalPages": 2,
  "pageSize": 10
}
```

---

### 4. Many-to-Many Relationship ✅ (3 Marks)
**User ↔ Role** — one user can have many roles, one role can belong to many users.

Join table: `user_roles (user_id, role_id)`

```java
// In User.java
@ManyToMany(fetch = FetchType.EAGER)
@JoinTable(
    name = "user_roles",
    joinColumns = @JoinColumn(name = "user_id"),
    inverseJoinColumns = @JoinColumn(name = "role_id")
)
private Set<Role> roles;

// In Role.java
@ManyToMany(mappedBy = "roles")
private Set<User> users;
```

**Assign role to user:**
```
POST /api/roles/{roleId}/assign/{userId}
```

---

### 5. One-to-Many Relationship ✅ (2 Marks)
**Church → Members** — one church has many members.
**Location → Children** — one province has many districts, etc.

```java
// In Church.java
@OneToMany(mappedBy = "church", cascade = CascadeType.ALL)
private List<Member> members;

// In Member.java
@ManyToOne
@JoinColumn(name = "church_id", nullable = false)
private Church church;
```

Also in `Location.java`:
```java
@OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
private List<Location> children;
```

---

### 6. One-to-One Relationship ✅ (2 Marks)
**User ↔ UserProfile** — each user has exactly one profile.
**Church ↔ Location** — each church is at exactly one village.

```java
// In User.java
@OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
private UserProfile profile;

// In UserProfile.java (owning side — holds the FK)
@OneToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "user_id", nullable = false)
private User user;
```

---

### 7. existBy() Method ✅ (2 Marks)

Spring Data JPA generates an existence check query that returns `true` or `false` without loading the full object. It is faster than `findBy` because it stops at the first match.

**UserRepository.java**
```java
// Checks if a user with this email already exists before saving
// Prevents duplicate accounts
boolean existsByEmail(String email);
```

**LocationRepository.java**
```java
// Checks if a location code already exists before creating
// Prevents duplicate province/district/sector/cell/village codes
boolean existsByCode(String code);
```

**RoleRepository.java**
```java
// Checks if a role name already exists before creating
// Prevents duplicate roles like two PASTOR entries
boolean existsByName(String name);
```

**ChurchRepository.java**
```java
// Checks if a church name already exists before saving
// Prevents duplicate church registrations
boolean existsByName(String name);
```

**MemberRepository.java**
```java
// Checks if a membership number already exists before saving
// Prevents duplicate membership records
boolean existsByMembershipNumber(String membershipNumber);
```

**How it is used in service layer (UserService.java):**
```java
public User createUser(User user) {
    // existsByEmail returns boolean — fast, no full object loaded
    if (userRepository.existsByEmail(user.getEmail())) {
        throw new RuntimeException("Email already exists: " + user.getEmail());
    }
    return userRepository.save(user);
}
```

**REST endpoints to test existBy:**
```
GET /api/users/check-exists?email=alice@church.rw   → { "exists": true }
GET /api/roles/check-exists?name=PASTOR             → { "exists": true }
GET /api/locations/check/KGL                        → true
GET /api/churches/check-exists?name=ADRA Church     → { "exists": false }
```

---

### 8. Retrieve Users by Province ✅ (4 Marks)

Users are linked to **Village level only**. To retrieve by Province, the JPQL query navigates up 4 parent hops through the self-referenced `locations` table:

```
User → l1 (Village) → l2 (Cell) → l3 (Sector) → l4 (District) → l5 (Province)
```

**UserRepository.java — retrieve by province CODE:**
```java
@Query("SELECT u FROM User u " +
       "JOIN u.location l1 " +        // l1 = Village (where user lives)
       "JOIN l1.parent l2 " +         // l2 = Cell
       "JOIN l2.parent l3 " +         // l3 = Sector
       "JOIN l3.parent l4 " +         // l4 = District
       "JOIN l4.parent l5 " +         // l5 = Province
       "WHERE l5.code = :provinceCode")
Page<User> findByProvinceCode(@Param("provinceCode") String provinceCode, Pageable pageable);
```

**UserRepository.java — retrieve by province NAME (case-insensitive):**
```java
@Query("SELECT u FROM User u " +
       "JOIN u.location l1 " +
       "JOIN l1.parent l2 " +
       "JOIN l2.parent l3 " +
       "JOIN l3.parent l4 " +
       "JOIN l4.parent l5 " +
       "WHERE LOWER(l5.name) = LOWER(:provinceName)")
Page<User> findByProvinceName(@Param("provinceName") String provinceName, Pageable pageable);
```

**UserService.java — calls the repository:**
```java
public Page<User> getUsersByProvinceCode(String provinceCode, int page, int size) {
    Pageable pageable = PageRequest.of(page, size);
    return userRepository.findByProvinceCode(provinceCode, pageable);
}

public Page<User> getUsersByProvinceName(String provinceName, int page, int size) {
    Pageable pageable = PageRequest.of(page, size);
    return userRepository.findByProvinceName(provinceName, pageable);
}
```

**REST endpoints to test:**
```
GET /api/users/province/code/KGL
GET /api/users/province/name/Kigali City
GET /api/users/province/code/SP
GET /api/users/province/name/Southern Province
GET /api/users/province/code/NP
GET /api/users/province/code/WP
```

**Sample response:**
```json
{
  "users": [
    { "id": 1, "firstName": "Alice", "lastName": "Uwimana", "email": "alice@church.rw" },
    { "id": 2, "firstName": "Bob",   "lastName": "Nkurunziza", "email": "bob@church.rw" }
  ],
  "provinceCode": "KGL",
  "currentPage": 0,
  "totalItems": 8,
  "totalPages": 1
}
```

---

## All API Endpoints

### Locations — `/api/locations`
| Method | URL | Description |
|--------|-----|-------------|
| POST | `/api/locations` | Create location |
| GET | `/api/locations` | Get all locations |
| GET | `/api/locations/{id}` | Get by UUID |
| GET | `/api/locations/code/{code}` | Get by code |
| GET | `/api/locations/provinces` | Get all provinces |
| GET | `/api/locations/type/{type}` | Get by type |
| GET | `/api/locations/{parentId}/children` | Get children |
| GET | `/api/locations/check/{code}` | existBy check |
| PUT | `/api/locations/{id}` | Update location |
| DELETE | `/api/locations/{id}` | Delete location |

### Users — `/api/users`
| Method | URL | Description |
|--------|-----|-------------|
| POST | `/api/users` | Create user (with village ID) |
| POST | `/api/users/by-location/{code}` | Create user by village code |
| GET | `/api/users?page=0&size=10&sortBy=firstName&sortDir=ASC` | Get all (paginated + sorted) |
| GET | `/api/users/sorted?sortBy=lastName&sortDir=DESC` | Get all sorted |
| GET | `/api/users/{id}` | Get by ID |
| GET | `/api/users/check-exists?email=x` | existBy check |
| GET | `/api/users/province/code/{code}` | Get by province code |
| GET | `/api/users/province/name/{name}` | Get by province name |
| GET | `/api/users/district/{code}` | Get by district |
| GET | `/api/users/sector/{code}` | Get by sector |
| GET | `/api/users/cell/{code}` | Get by cell |
| PUT | `/api/users/{id}` | Update user |
| DELETE | `/api/users/{id}` | Delete user |

### Roles — `/api/roles`
| Method | URL | Description |
|--------|-----|-------------|
| POST | `/api/roles` | Create role |
| GET | `/api/roles` | Get all roles |
| GET | `/api/roles/{id}` | Get by ID |
| GET | `/api/roles/check-exists?name=x` | existBy check |
| GET | `/api/roles/{roleId}/users` | Get users with this role |
| POST | `/api/roles/{roleId}/assign/{userId}` | Assign role to user |
| DELETE | `/api/roles/{roleId}/remove/{userId}` | Remove role from user |
| PUT | `/api/roles/{id}` | Update role |
| DELETE | `/api/roles/{id}` | Delete role |

### User Profiles — `/api/profiles`
| Method | URL | Description |
|--------|-----|-------------|
| POST | `/api/profiles` | Create profile |
| GET | `/api/profiles` | Get all profiles |
| GET | `/api/profiles/{id}` | Get by ID |
| GET | `/api/profiles/user/{userId}` | Get by user ID |
| PUT | `/api/profiles/{id}` | Update profile |
| DELETE | `/api/profiles/{id}` | Delete profile |

### Churches — `/api/churches`
| Method | URL | Description |
|--------|-----|-------------|
| POST | `/api/churches` | Create church |
| GET | `/api/churches` | Get all churches |
| GET | `/api/churches/{id}` | Get by ID |
| GET | `/api/churches/check-exists?name=x` | existBy check |
| PUT | `/api/churches/{id}` | Update church |
| DELETE | `/api/churches/{id}` | Delete church |

### Members — `/api/members`
| Method | URL | Description |
|--------|-----|-------------|
| POST | `/api/members` | Create member |
| GET | `/api/members` | Get all members |
| GET | `/api/members/{id}` | Get by ID |
| GET | `/api/members/church/{churchId}?page=0&size=10` | Get members of a church |
| GET | `/api/members/province/code/{code}` | Get members by province code |
| GET | `/api/members/province/name/{name}` | Get members by province name |
| PUT | `/api/members/{id}` | Update member |
| DELETE | `/api/members/{id}` | Delete member |

---

## Viva-Voce Quick Reference

| Question | Answer |
|----------|--------|
| What is JPA? | Java Persistence API — maps Java objects to database tables |
| What is Hibernate? | JPA implementation that auto-generates SQL |
| What is a foreign key? | A column referencing the primary key of another table |
| What is Pageable? | Spring interface handling page number, size and sorting |
| What is CascadeType.ALL? | Operations on parent (save/delete) automatically apply to children |
| What is @Entity? | Marks a Java class as a database table |
| What is @Repository? | Marks an interface as the data access layer |
| What is existsBy()? | Returns boolean — checks existence without loading full object |
| What is @JoinTable? | Defines the join table for Many-to-Many relationships |
| What is mappedBy? | Marks the non-owning side of a relationship |
| What is @OneToOne? | Maps a relationship where each entity has exactly one of the other |
| What is self-referencing? | An entity that has a FK pointing to its own table (Location hierarchy) |

---

## Common Issues

| Problem | Fix |
|---------|-----|
| `mvn` not recognized | Run: `$env:PATH += ";C:\Users\HP\maven\apache-maven-3.9.6\bin"` |
| Port already in use | Run: `netstat -ano \| findstr ":8082"` then `taskkill /PID <pid> /F` |
| Database not found | Run: `psql -U postgres -c "CREATE DATABASE church_db;"` |
| data.sql errors | Check FK order — delete children before parents |
