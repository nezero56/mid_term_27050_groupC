// FILE: src/main/java/rw/churchmanagement/repository/LocationRepository.java

package rw.churchmanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rw.churchmanagement.model.Location;
import rw.churchmanagement.model.Location.LocationType;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * STEP 3: CREATE REPOSITORY
 * 
 * WHAT IS A REPOSITORY?
 * - Interface that handles database operations (CRUD)
 * - Spring Data JPA automatically implements these methods
 * - You just declare method signatures, Spring does the rest!
 */

@Repository
public interface LocationRepository extends JpaRepository<Location, UUID> {
    
    // SPRING DATA JPA MAGIC:
    // Just by writing method names following conventions,
    // Spring automatically creates the SQL queries!
    
    // Find by code
    // SQL: SELECT * FROM locations WHERE code = ?
    Optional<Location> findByCode(String code);
    
    // Check if exists by code
    // SQL: SELECT COUNT(*) FROM locations WHERE code = ?
    boolean existsByCode(String code);
    
    // Find by location type
    // SQL: SELECT * FROM locations WHERE location_type = ?
    List<Location> findByLocationType(LocationType locationType);
    
    // Find by parent
    // SQL: SELECT * FROM locations WHERE parent_id = ?
    List<Location> findByParent(Location parent);
    
    // Find by type and parent
    // SQL: SELECT * FROM locations WHERE location_type = ? AND parent_id = ?
    List<Location> findByLocationTypeAndParent(LocationType locationType, Location parent);
    
    // Find by type where parent is null (provinces)
    // SQL: SELECT * FROM locations WHERE location_type = ? AND parent_id IS NULL
    List<Location> findByLocationTypeAndParentIsNull(LocationType locationType);
}

/**
 * SPRING DATA JPA METHOD NAMING CONVENTIONS:
 * 
 * findBy...        - SELECT query
 * existsBy...      - Check if exists (returns boolean)
 * countBy...       - Count records
 * deleteBy...      - DELETE query
 * 
 * And                - Combines conditions with AND
 * Or                 - Combines conditions with OR
 * IsNull             - WHERE field IS NULL
 * IsNotNull          - WHERE field IS NOT NULL
 * Like               - WHERE field LIKE '%value%'
 * OrderBy...Asc      - ORDER BY field ASC
 * OrderBy...Desc     - ORDER BY field DESC
 * 
 * EXAMPLES:
 * findByNameAndCode(String name, String code)
 * findByLocationTypeOrderByNameAsc(LocationType type)
 * countByParentIsNull()
 */
