// FILE: src/main/java/rw/churchmanagement/dto/LocationDTO.java

package rw.churchmanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import rw.churchmanagement.model.Location.LocationType;

import java.util.UUID;

/**
 * STEP 2: CREATE DTO (Data Transfer Object)
 * 
 * WHY USE DTO?
 * - Separates API input/output from database entities
 * - Allows you to control what data is sent/received
 * - Prevents exposing internal entity structure
 * - Makes it easier to handle parent relationships
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LocationDTO {
    
    // Fields that will be sent in JSON request
    private String code;
    private String name;
    private LocationType locationType;
    private UUID parentId;  // Just the parent ID, not the whole parent object
}

/**
 * EXAMPLE JSON REQUEST:
 * 
 * {
 *   "code": "KGL",
 *   "name": "Kigali",
 *   "locationType": "PROVINCE",
 *   "parentId": null
 * }
 * 
 * OR for a district:
 * 
 * {
 *   "code": "KCK",
 *   "name": "Kicukiro",
 *   "locationType": "DISTRICT",
 *   "parentId": "a1b2c3d4-e5f6-7890-abcd-ef1234567890"
 * }
 */
