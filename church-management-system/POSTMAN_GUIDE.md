# Postman API Guide - Location Management

## Overview
This guide shows how to insert locations using Postman. Locations use **UUID** for IDs (auto-generated) and follow a hierarchical structure.

---

## Code Format Examples

| Location Type | Name | Code |
|--------------|------|------|
| Province | Kigali | KGL |
| Province | Southern Province | SP |
| Province | Western Province | WP |
| Province | Northern Province | NP |
| Province | Eastern Province | EP |
| District | Kicukiro | KCK |
| District | Gasabo | GSB |
| District | Nyarugenge | NYR |
| Sector | Kicukiro Sector | KCK-S1 |
| Sector | Niboye Sector | KCK-S2 |
| Cell | Kicukiro Cell | KCK-S1-C1 |
| Cell | Kagugu Cell | KCK-S1-C2 |
| Village | Village 1 | KCK-S1-C1-V1 |
| Village | Village 2 | KCK-S1-C1-V2 |

---

## API Endpoints

### Base URL
```
http://localhost:8080/api/locations
```

---

## Step-by-Step: Creating Location Hierarchy

### Step 1: Create a Province (No Parent)

**Endpoint:** `POST http://localhost:8080/api/locations`

**Headers:**
```
Content-Type: application/json
```

**Body (JSON):**
```json
{
  "code": "KGL",
  "name": "Kigali",
  "locationType": "PROVINCE"
}
```

**Response (Example):**
```json
{
  "id": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
  "code": "KGL",
  "name": "Kigali",
  "locationType": "PROVINCE",
  "parent": null,
  "children": []
}
```

**Save the `id` from the response - you'll need it for creating districts!**

---

### Step 2: Create a District (With Parent Province)

**METHOD 1: Using Query Parameter (RECOMMENDED)**

**Endpoint:** `POST http://localhost:8080/api/locations/with-parent?parentId={PROVINCE_UUID}`

Replace `{PROVINCE_UUID}` with the UUID from Step 1.

**Example:**
```
POST http://localhost:8080/api/locations/with-parent?parentId=a1b2c3d4-e5f6-7890-abcd-ef1234567890
```

**Headers:**
```
Content-Type: application/json
```

**Body (JSON):**
```json
{
  "code": "KCK",
  "name": "Kicukiro",
  "locationType": "DISTRICT"
}
```

**METHOD 2: Including Parent in Body**

**Endpoint:** `POST http://localhost:8080/api/locations`

**Body (JSON):**
```json
{
  "code": "KCK",
  "name": "Kicukiro",
  "locationType": "DISTRICT",
  "parent": {
    "id": "a1b2c3d4-e5f6-7890-abcd-ef1234567890"
  }
}
```

**Response (Example):**
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
  },
  "children": []
}
```

**Save the district `id` for creating sectors!**

---

### Step 3: Create a Sector (With Parent District)

**METHOD 1: Using Query Parameter**

**Endpoint:** `POST http://localhost:8080/api/locations/with-parent?parentId={DISTRICT_UUID}`

**Example:**
```
POST http://localhost:8080/api/locations/with-parent?parentId=b2c3d4e5-f6a7-8901-bcde-f12345678901
```

**Body (JSON):**
```json
{
  "code": "KCK-S1",
  "name": "Kicukiro Sector",
  "locationType": "SECTOR"
}
```

**METHOD 2: Including Parent in Body**

**Endpoint:** `POST http://localhost:8080/api/locations`

**Body (JSON):**
```json
{
  "code": "KCK-S1",
  "name": "Kicukiro Sector",
  "locationType": "SECTOR",
  "parent": {
    "id": "b2c3d4e5-f6a7-8901-bcde-f12345678901"
  }
}
```

---

### Step 4: Create a Cell (With Parent Sector)

**Endpoint:** `POST http://localhost:8080/api/locations/with-parent?parentId={SECTOR_UUID}`

**Body (JSON):**
```json
{
  "code": "KCK-S1-C1",
  "name": "Kicukiro Cell",
  "locationType": "CELL"
}
```

**OR with parent in body:**
```json
{
  "code": "KCK-S1-C1",
  "name": "Kicukiro Cell",
  "locationType": "CELL",
  "parent": {
    "id": "{SECTOR_UUID}"
  }
}
```

---

### Step 5: Create a Village (With Parent Cell)

**Endpoint:** `POST http://localhost:8080/api/locations/with-parent?parentId={CELL_UUID}`

**Body (JSON):**
```json
{
  "code": "KCK-S1-C1-V1",
  "name": "Village 1",
  "locationType": "VILLAGE"
}
```

**OR with parent in body:**
```json
{
  "code": "KCK-S1-C1-V1",
  "name": "Village 1",
  "locationType": "VILLAGE",
  "parent": {
    "id": "{CELL_UUID}"
  }
}
```

---

## Alternative: Create Location Without Specifying Parent in URL

You can include the parent directly in the request body:

**Endpoint:** `POST http://localhost:8080/api/locations`

**Body (JSON):**
```json
{
  "code": "GSB",
  "name": "Gasabo",
  "locationType": "DISTRICT",
  "parent": {
    "id": "a1b2c3d4-e5f6-7890-abcd-ef1234567890"
  }
}
```

**Important:** When including parent in body, you ONLY need to provide the parent's `id`. The system will automatically fetch the full parent details.

---

## Query Endpoints

### Get All Provinces
```
GET http://localhost:8080/api/locations/provinces
```

### Get All Locations
```
GET http://localhost:8080/api/locations
```

### Get Location by ID
```
GET http://localhost:8080/api/locations/{UUID}
```

### Get Location by Code
```
GET http://localhost:8080/api/locations/code/KGL
```

### Get Children of a Location
```
GET http://localhost:8080/api/locations/{PARENT_UUID}/children
```

### Get Districts of a Province
```
GET http://localhost:8080/api/locations/{PROVINCE_UUID}/children/type/DISTRICT
```

### Get All Locations of a Specific Type
```
GET http://localhost:8080/api/locations/type/PROVINCE
GET http://localhost:8080/api/locations/type/DISTRICT
GET http://localhost:8080/api/locations/type/SECTOR
GET http://localhost:8080/api/locations/type/CELL
GET http://localhost:8080/api/locations/type/VILLAGE
```

### Check if Location Code Exists
```
GET http://localhost:8080/api/locations/check/KGL
```

---

## Complete Example: Creating Full Hierarchy

### 1. Create Kigali Province
```json
POST http://localhost:8080/api/locations
{
  "code": "KGL",
  "name": "Kigali",
  "locationType": "PROVINCE",
  "parent": null
}
```
**Response ID:** `a1b2c3d4-e5f6-7890-abcd-ef1234567890`

### 2. Create Kicukiro District
```json
POST http://localhost:8080/api/locations/with-parent?parentId=a1b2c3d4-e5f6-7890-abcd-ef1234567890
{
  "code": "KCK",
  "name": "Kicukiro",
  "locationType": "DISTRICT"
}
```
**Response ID:** `b2c3d4e5-f6a7-8901-bcde-f12345678901`

### 3. Create Kicukiro Sector
```json
POST http://localhost:8080/api/locations/with-parent?parentId=b2c3d4e5-f6a7-8901-bcde-f12345678901
{
  "code": "KCK-S1",
  "name": "Kicukiro Sector",
  "locationType": "SECTOR"
}
```
**Response ID:** `c3d4e5f6-a7b8-9012-cdef-123456789012`

### 4. Create Kicukiro Cell
```json
POST http://localhost:8080/api/locations/with-parent?parentId=c3d4e5f6-a7b8-9012-cdef-123456789012
{
  "code": "KCK-S1-C1",
  "name": "Kicukiro Cell",
  "locationType": "CELL"
}
```
**Response ID:** `d4e5f6a7-b8c9-0123-def1-234567890123`

### 5. Create Village 1
```json
POST http://localhost:8080/api/locations/with-parent?parentId=d4e5f6a7-b8c9-0123-def1-234567890123
{
  "code": "KCK-S1-C1-V1",
  "name": "Village 1",
  "locationType": "VILLAGE"
}
```

---

## Suggested Code Naming Convention

### Provinces
- **KGL** - Kigali
- **SP** - Southern Province
- **WP** - Western Province
- **NP** - Northern Province
- **EP** - Eastern Province

### Districts (Province Code + District Abbreviation)
- **KGL-KCK** or **KCK** - Kicukiro
- **KGL-GSB** or **GSB** - Gasabo
- **KGL-NYR** or **NYR** - Nyarugenge

### Sectors (District Code + Sector Number)
- **KCK-S1** - Kicukiro Sector 1
- **KCK-S2** - Niboye Sector
- **GSB-S1** - Kacyiru Sector

### Cells (Sector Code + Cell Number)
- **KCK-S1-C1** - Kicukiro Cell 1
- **KCK-S1-C2** - Kagugu Cell

### Villages (Cell Code + Village Number)
- **KCK-S1-C1-V1** - Village 1
- **KCK-S1-C1-V2** - Village 2

---

## Important Notes

1. **UUID Auto-Generated**: IDs are automatically generated as UUIDs
2. **Parent-Child Relationship**: Always create parent before children
3. **Unique Codes**: Each location code must be unique
4. **Location Types**: Must be one of: PROVINCE, DISTRICT, SECTOR, CELL, VILLAGE
5. **Save UUIDs**: Keep track of parent UUIDs when creating child locations

---

## Testing the Hierarchy

After creating locations, verify the hierarchy:

```
GET http://localhost:8080/api/locations/code/KGL
```

This will show the province with all its nested children.

---

## Author
Sonia Munezero

## Date
March 13, 2026
