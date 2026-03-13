# Church Management System - Self-Referenced Location Structure

## Overview
This project has been refactored to use a **self-referenced Location table** for managing Rwanda's administrative hierarchy instead of separate tables for Province, District, Sector, Cell, and Village.

## Database Structure

### Location Table (Self-Referenced)
The `locations` table now handles all administrative levels using a parent-child relationship:

```sql
CREATE TABLE locations (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    code VARCHAR(255) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    location_type VARCHAR(50) NOT NULL,  -- PROVINCE, DISTRICT, SECTOR, CELL, VILLAGE
    parent_id BIGINT,
    FOREIGN KEY (parent_id) REFERENCES locations(id)
);
```

### Hierarchy Structure
```
Province (parent_id = NULL)
  └── District (parent_id = Province.id)
      └── Sector (parent_id = District.id)
          └── Cell (parent_id = Sector.id)
              └── Village (parent_id = Cell.id)
```

## Key Features

### 1. Single Table Design
- All location types stored in one table
- `location_type` enum: PROVINCE, DISTRICT, SECTOR, CELL, VILLAGE
- Self-referencing `parent_id` creates the hierarchy

### 2. Location Entity
```java
@Entity
@Table(name = "locations")
public class Location {
    private Long id;
    private String code;
    private String name;
    private LocationType locationType;
    
    @ManyToOne
    private Location parent;
    
    @OneToMany(mappedBy = "parent")
    private List<Location> children;
}
```

### 3. UserProfile Integration
UserProfile now references Location directly:
```java
@ManyToOne
@JoinColumn(name = "location_id")
private Location location;
```

When a user is assigned a Village, you can traverse up the hierarchy:
```
Village → Cell → Sector → District → Province
```

## API Endpoints

### General Location Endpoints
- `GET /api/locations` - Get all locations
- `GET /api/locations/{id}` - Get location by ID
- `GET /api/locations/code/{code}` - Get location by code
- `POST /api/locations` - Create a location
- `POST /api/locations/with-parent?parentId={id}` - Create location with parent
- `GET /api/locations/check/{code}` - Check if location exists

### Hierarchy-Specific Endpoints
- `GET /api/locations/provinces` - Get all provinces (locations with no parent)
- `GET /api/locations/type/{type}` - Get all locations of a specific type
- `GET /api/locations/{parentId}/children` - Get all children of a location
- `GET /api/locations/{parentId}/children/type/{type}` - Get children of specific type

## Benefits

1. **Simplified Schema**: One table instead of five
2. **Flexible Hierarchy**: Easy to add new levels if needed
3. **Consistent Relationships**: Same pattern throughout
4. **Easier Queries**: Recursive queries for hierarchy traversal
5. **Better Maintainability**: Single entity to manage

## Sample Data
The `data.sql` file includes:
- 5 Provinces
- 30 Districts
- 35 Sectors (sample)
- 13 Cells (sample)
- 20 Villages (sample)

## Usage Example

### Creating a Complete Hierarchy
```java
// Create Province
Location kigali = new Location("01", "Kigali City", LocationType.PROVINCE, null);

// Create District
Location gasabo = new Location("0102", "Gasabo", LocationType.DISTRICT, kigali);

// Create Sector
Location kacyiru = new Location("010201", "Kacyiru", LocationType.SECTOR, gasabo);

// Create Cell
Location cell = new Location("01020101", "Kacyiru Cell", LocationType.CELL, kacyiru);

// Create Village
Location village = new Location("0102010101", "Village 1", LocationType.VILLAGE, cell);

// Save with cascade
locationService.saveCompleteHierarchy(kigali);
```

### Querying Hierarchy
```java
// Get all provinces
List<Location> provinces = locationService.getProvinces();

// Get districts in a province
List<Location> districts = locationService.getChildLocations(provinceId);

// Get villages in a cell
List<Location> villages = locationService.getLocationsByTypeAndParent(LocationType.VILLAGE, cellId);
```

## Migration Notes

### Removed Files
- `Province.java`, `District.java`, `Sector.java`, `Cell.java`, `Village.java`
- `ProvinceRepository.java`, `DistrictRepository.java`, `SectorRepository.java`, `CellRepository.java`, `VillageRepository.java`

### New Files
- `Location.java` - Single entity for all location types
- `LocationRepository.java` - Repository with hierarchy queries

### Updated Files
- `LocationService.java` - Simplified service methods
- `LocationController.java` - Unified REST endpoints
- `UserProfile.java` - Now references Location instead of Village
- `data.sql` - Restructured for single table

## Author
Sonia Munezero
