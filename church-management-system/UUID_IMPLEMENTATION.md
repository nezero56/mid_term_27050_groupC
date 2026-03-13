# ✅ UUID Implementation & Manual Data Insertion - COMPLETE!

## Summary of Changes

Your Church Management System now uses **UUID for location IDs** and requires **manual data insertion via Postman**.

---

## What Changed

### 1. ✅ UUID for Location IDs
- Changed from `Long` to `UUID` for location IDs
- IDs are auto-generated using Hibernate's UUID generator
- Format: `a1b2c3d4-e5f6-7890-abcd-ef1234567890`

### 2. ✅ No Automatic Location Data
- Removed automatic location data insertion
- Only roles are loaded automatically
- All locations must be created via Postman API

### 3. ✅ Fixed Parent ID Issue
- Updated `saveLocation()` method to properly handle parent IDs
- When you send a location with `parent: { "id": "uuid" }`, it now correctly fetches and sets the parent

### 4. ✅ Code Format Examples
- Province: **KGL** (Kigali), **SP** (Southern Province)
- District: **KCK** (Kicukiro), **GSB** (Gasabo)
- Sector: **KCK-S1** (Kicukiro Sector 1)
- Cell: **KCK-S1-C1** (Kicukiro Sector 1 Cell 1)
- Village: **KCK-S1-C1-V1** (Village 1)

---

## How to Create Locations

### Method 1: Using Query Parameter (Recommended)

**Create Province:**
```json
POST http://localhost:8080/api/locations
{
  "code": "KGL",
  "name": "Kigali",
  "locationType": "PROVINCE"
}
```

**Create District with Parent:**
```json
POST http://localhost:8080/api/locations/with-parent?parentId={PROVINCE_UUID}
{
  "code": "KCK",
  "name": "Kicukiro",
  "locationType": "DISTRICT"
}
```

### Method 2: Including Parent in Body

**Create District:**
```json
POST http://localhost:8080/api/locations
{
  "code": "KCK",
  "name": "Kicukiro",
  "locationType": "DISTRICT",
  "parent": {
    "id": "a1b2c3d4-e5f6-7890-abcd-ef1234567890"
  }
}
```

**Important:** You only need to provide the parent's `id`. The system automatically fetches the full parent details.

---

## Files Modified

### Entity
- ✅ `Location.java` - Changed ID from `Long` to `UUID`

### Repository
- ✅ `LocationRepository.java` - Updated to use `UUID`

### Service
- ✅ `LocationService.java` - Updated to use `UUID` and fixed parent handling

### Controller
- ✅ `LocationController.java` - Updated to use `UUID`

### Data
- ✅ `data.sql` - Removed location data, kept only roles

### Configuration
- ✅ `application.properties` - Disabled schema.sql loading

---

## Database Schema

### Locations Table (PostgreSQL)
```sql
CREATE TABLE locations (
    id UUID PRIMARY KEY,
    code VARCHAR(255) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    location_type VARCHAR(255) NOT NULL,
    parent_id UUID REFERENCES locations(id)
);
```

---

## Testing Steps

### 1. Start the Application
```bash
mvn spring-boot:run
```

### 2. Create a Province
```bash
POST http://localhost:8080/api/locations
Content-Type: application/json

{
  "code": "KGL",
  "name": "Kigali",
  "locationType": "PROVINCE"
}
```

**Response:**
```json
{
  "id": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
  "code": "KGL",
  "name": "Kigali",
  "locationType": "PROVINCE",
  "parent": null
}
```

### 3. Create a District
```bash
POST http://localhost:8080/api/locations
Content-Type: application/json

{
  "code": "KCK",
  "name": "Kicukiro",
  "locationType": "DISTRICT",
  "parent": {
    "id": "a1b2c3d4-e5f6-7890-abcd-ef1234567890"
  }
}
```

**Response:**
```json
{
  "id": "b2c3d4e5-f6a7-8901-bcde-f12345678901",
  "code": "KCK",
  "name": "Kicukiro",
  "locationType": "DISTRICT",
  "parent": {
    "id": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
    "code": "KGL",
    "name": "Kigali",
    "locationType": "PROVINCE"
  }
}
```

### 4. Verify Parent-Child Relationship
```bash
GET http://localhost:8080/api/locations/{PROVINCE_UUID}/children
```

This will return all districts under the province.

---

## Key Features

### ✅ UUID Auto-Generation
- IDs are automatically generated as UUIDs
- No need to specify ID when creating locations
- Ensures uniqueness across distributed systems

### ✅ Parent-Child Relationship
- Parent ID is properly saved and maintained
- Can query children by parent ID
- Hierarchical structure is preserved

### ✅ Two Ways to Create Locations
1. **Query Parameter:** `/with-parent?parentId={UUID}`
2. **Request Body:** Include `parent: { "id": "uuid" }`

### ✅ Flexible Code Format
- Use any code format you prefer
- Examples: KGL, KCK, KCK-S1, etc.
- Codes must be unique

---

## Documentation

- **[POSTMAN_GUIDE.md](POSTMAN_GUIDE.md)** - Complete Postman API guide with examples
- **[SETUP_GUIDE.md](SETUP_GUIDE.md)** - Application setup instructions
- **[LOCATION_STRUCTURE.md](LOCATION_STRUCTURE.md)** - Location hierarchy details

---

## Build Status

```
[INFO] BUILD SUCCESS
[INFO] Compiling 13 source files
```

✅ Project compiles successfully!

---

## What Gets Loaded Automatically

| Table | Records | Description |
|-------|---------|-------------|
| `locations` | 0 | Empty - insert via Postman |
| `roles` | 6 | Auto-loaded (MEMBER, LEADER, TREASURER, PASTOR, SECRETARY, CHOIR_LEADER) |
| `users` | 0 | Empty - ready for data |
| `user_profiles` | 0 | Empty - ready for data |
| `user_roles` | 0 | Empty - ready for data |

---

## Next Steps

1. ✅ Start the application: `mvn spring-boot:run`
2. ✅ Open Postman
3. ✅ Create provinces using POST requests
4. ✅ Create districts with parent province UUIDs
5. ✅ Continue creating sectors, cells, and villages
6. ✅ Verify hierarchy using GET endpoints

---

## Author
Sonia Munezero

## Date
March 13, 2026

---

# 🎉 Ready to Use!

The parent ID issue is fixed. Now when you create a location with a parent, the parent_id will be properly saved in the database!
