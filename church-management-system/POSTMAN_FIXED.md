# FIXED! Postman Guide - Parent ID Now Works

## The Fix
Created a **LocationDTO** to properly handle parent ID. Now parent_id will be saved correctly!

---

## How to Create Locations (NEW FORMAT)

### 1. Create Province (No Parent)

**Endpoint:** `POST http://localhost:8080/api/locations`

**Body:**
```json
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

---

### 2. Create District (With Parent)

**Endpoint:** `POST http://localhost:8080/api/locations`

**Body:**
```json
{
  "code": "KCK",
  "name": "Kicukiro",
  "locationType": "DISTRICT",
  "parentId": "a1b2c3d4-e5f6-7890-abcd-ef1234567890"
}
```

**Note:** Use `parentId` (not `parent: { "id": "..." }`)

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

---

### 3. Create Sector

**Body:**
```json
{
  "code": "KCK-S1",
  "name": "Kicukiro Sector",
  "locationType": "SECTOR",
  "parentId": "b2c3d4e5-f6a7-8901-bcde-f12345678901"
}
```

---

### 4. Create Cell

**Body:**
```json
{
  "code": "KCK-S1-C1",
  "name": "Kicukiro Cell",
  "locationType": "CELL",
  "parentId": "{SECTOR_UUID}"
}
```

---

### 5. Create Village

**Body:**
```json
{
  "code": "KCK-S1-C1-V1",
  "name": "Village 1",
  "locationType": "VILLAGE",
  "parentId": "{CELL_UUID}"
}
```

---

## Complete Example

### Step 1: Create Kigali Province
```json
POST http://localhost:8080/api/locations
{
  "code": "KGL",
  "name": "Kigali",
  "locationType": "PROVINCE"
}
```
**Copy the `id` from response**

### Step 2: Create Kicukiro District
```json
POST http://localhost:8080/api/locations
{
  "code": "KCK",
  "name": "Kicukiro",
  "locationType": "DISTRICT",
  "parentId": "PASTE_PROVINCE_ID_HERE"
}
```

### Step 3: Create Kicukiro Sector
```json
POST http://localhost:8080/api/locations
{
  "code": "KCK-S1",
  "name": "Kicukiro Sector",
  "locationType": "SECTOR",
  "parentId": "PASTE_DISTRICT_ID_HERE"
}
```

### Step 4: Create Kicukiro Cell
```json
POST http://localhost:8080/api/locations
{
  "code": "KCK-S1-C1",
  "name": "Kicukiro Cell",
  "locationType": "CELL",
  "parentId": "PASTE_SECTOR_ID_HERE"
}
```

### Step 5: Create Village
```json
POST http://localhost:8080/api/locations
{
  "code": "KCK-S1-C1-V1",
  "name": "Village 1",
  "locationType": "VILLAGE",
  "parentId": "PASTE_CELL_ID_HERE"
}
```

---

## Query Endpoints

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

### Get All Provinces
```
GET http://localhost:8080/api/locations/provinces
```

### Get Children of a Location
```
GET http://localhost:8080/api/locations/{PARENT_UUID}/children
```

---

## Key Changes

✅ **Use `parentId` field** - Not `parent: { "id": "..." }`
✅ **Parent ID is now saved correctly** - No more null parent_id
✅ **Simpler JSON format** - Just provide the UUID directly

---

## Testing

1. Start application (make sure port 8080 is free)
2. Create a province
3. Copy the province ID from response
4. Create a district with that province ID as `parentId`
5. Verify parent_id is saved in database

**The parent_id will now be saved correctly!** 🎉
