// FILE: src/main/java/rw/churchmanagement/controller/LocationController.java

package rw.churchmanagement.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rw.churchmanagement.dto.LocationDTO;
import rw.churchmanagement.model.Location;
import rw.churchmanagement.model.Location.LocationType;
import rw.churchmanagement.service.LocationService;

import java.util.List;
import java.util.UUID;

/**
 * STEP 5: CREATE CONTROLLER (REST APIs)
 * 
 * WHAT IS A CONTROLLER?
 * - Handles HTTP requests (GET, POST, PUT, DELETE)
 * - Maps URLs to methods
 * - Returns HTTP responses
 * - This is where your APIs are created!
 */

@RestController                          // Marks this as a REST controller
@RequestMapping("/api/locations")       // Base URL for all endpoints
public class LocationController {
    
    // Inject the service
    @Autowired
    private LocationService locationService;
    
    /**
     * API 1: CREATE LOCATION
     * 
     * Method: POST
     * URL: http://localhost:8080/api/locations
     * Body: JSON (LocationDTO)
     * 
     * Example Request:
     * {
     *   "code": "KGL",
     *   "name": "Kigali",
     *   "locationType": "PROVINCE"
     * }
     */
    @PostMapping                                    // POST request
    public ResponseEntity<Location> createLocation(@RequestBody LocationDTO dto) {
        Location saved = locationService.createLocation(dto);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);  // 201 Created
    }
    
    /**
     * API 2: GET ALL LOCATIONS
     * 
     * Method: GET
     * URL: http://localhost:8080/api/locations
     */
    @GetMapping                                     // GET request
    public ResponseEntity<List<Location>> getAllLocations() {
        return ResponseEntity.ok(locationService.getAllLocations());  // 200 OK
    }
    
    /**
     * API 3: GET LOCATION BY ID
     * 
     * Method: GET
     * URL: http://localhost:8080/api/locations/{id}
     * 
     * Example: http://localhost:8080/api/locations/a1b2c3d4-e5f6-7890-abcd-ef1234567890
     */
    @GetMapping("/{id}")                            // {id} is a path variable
    public ResponseEntity<Location> getLocationById(@PathVariable UUID id) {
        return locationService.getLocationById(id)
            .map(ResponseEntity::ok)                // If found, return 200 OK
            .orElse(ResponseEntity.notFound().build());  // If not found, return 404
    }
    
    /**
     * API 4: GET LOCATION BY CODE
     * 
     * Method: GET
     * URL: http://localhost:8080/api/locations/code/{code}
     * 
     * Example: http://localhost:8080/api/locations/code/KGL
     */
    @GetMapping("/code/{code}")
    public ResponseEntity<Location> getLocationByCode(@PathVariable String code) {
        return locationService.getLocationByCode(code)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * API 5: CREATE LOCATION WITH PARENT (Query Parameter)
     * 
     * Method: POST
     * URL: http://localhost:8080/api/locations/with-parent?parentId={uuid}
     * 
     * Example: http://localhost:8080/api/locations/with-parent?parentId=a1b2c3d4-e5f6-7890-abcd-ef1234567890
     */
    @PostMapping("/with-parent")
    public ResponseEntity<Location> createLocationWithParent(
            @RequestBody Location location,
            @RequestParam UUID parentId) {              // Query parameter
        Location saved = locationService.saveLocationWithParent(location, parentId);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }
    
    /**
     * API 6: CHECK IF LOCATION EXISTS
     * 
     * Method: GET
     * URL: http://localhost:8080/api/locations/check/{code}
     * 
     * Example: http://localhost:8080/api/locations/check/KGL
     * Response: true or false
     */
    @GetMapping("/check/{code}")
    public ResponseEntity<Boolean> checkLocationExists(@PathVariable String code) {
        return ResponseEntity.ok(locationService.locationExists(code));
    }
    
    /**
     * API 7: GET LOCATIONS BY TYPE
     * 
     * Method: GET
     * URL: http://localhost:8080/api/locations/type/{type}
     * 
     * Example: http://localhost:8080/api/locations/type/PROVINCE
     */
    @GetMapping("/type/{type}")
    public ResponseEntity<List<Location>> getLocationsByType(@PathVariable LocationType type) {
        return ResponseEntity.ok(locationService.getLocationsByType(type));
    }
    
    /**
     * API 8: GET ALL PROVINCES
     * 
     * Method: GET
     * URL: http://localhost:8080/api/locations/provinces
     */
    @GetMapping("/provinces")
    public ResponseEntity<List<Location>> getProvinces() {
        return ResponseEntity.ok(locationService.getProvinces());
    }
    
    /**
     * API 9: GET CHILDREN OF A LOCATION
     * 
     * Method: GET
     * URL: http://localhost:8080/api/locations/{parentId}/children
     * 
     * Example: http://localhost:8080/api/locations/a1b2c3d4-e5f6-7890-abcd-ef1234567890/children
     */
    @GetMapping("/{parentId}/children")
    public ResponseEntity<List<Location>> getChildLocations(@PathVariable UUID parentId) {
        return ResponseEntity.ok(locationService.getChildLocations(parentId));
    }
    
    /**
     * API 10: GET CHILDREN BY TYPE
     * 
     * Method: GET
     * URL: http://localhost:8080/api/locations/{parentId}/children/type/{type}
     * 
     * Example: http://localhost:8080/api/locations/a1b2c3d4-e5f6-7890-abcd-ef1234567890/children/type/DISTRICT
     */
    @GetMapping("/{parentId}/children/type/{type}")
    public ResponseEntity<List<Location>> getLocationsByTypeAndParent(
            @PathVariable UUID parentId,
            @PathVariable LocationType type) {
        return ResponseEntity.ok(locationService.getLocationsByTypeAndParent(type, parentId));
    }
}

/**
 * KEY ANNOTATIONS EXPLAINED:
 * 
 * @RestController - Combines @Controller and @ResponseBody
 *                   Automatically converts return values to JSON
 * 
 * @RequestMapping - Base URL for all endpoints in this controller
 * 
 * @GetMapping - Handles GET requests
 * @PostMapping - Handles POST requests
 * @PutMapping - Handles PUT requests
 * @DeleteMapping - Handles DELETE requests
 * 
 * @PathVariable - Extracts value from URL path
 *                 Example: /locations/{id} -> @PathVariable UUID id
 * 
 * @RequestParam - Extracts value from query string
 *                 Example: /locations?code=KGL -> @RequestParam String code
 * 
 * @RequestBody - Converts JSON request body to Java object
 * 
 * ResponseEntity - Wrapper for HTTP response
 *                  Allows you to set status code, headers, body
 * 
 * HTTP STATUS CODES:
 * 200 OK - Success
 * 201 Created - Resource created successfully
 * 204 No Content - Success but no data to return
 * 400 Bad Request - Invalid request
 * 404 Not Found - Resource not found
 * 500 Internal Server Error - Server error
 */

/**
 * HOW TO CREATE A NEW API:
 * 
 * 1. Choose HTTP method (@GetMapping, @PostMapping, etc.)
 * 2. Define URL path
 * 3. Add parameters (@PathVariable, @RequestParam, @RequestBody)
 * 4. Call service method
 * 5. Return ResponseEntity with appropriate status code
 * 
 * EXAMPLE - Create a new API to delete location:
 * 
 * @DeleteMapping("/{id}")
 * public ResponseEntity<Void> deleteLocation(@PathVariable UUID id) {
 *     locationService.deleteLocation(id);
 *     return ResponseEntity.noContent().build();  // 204 No Content
 * }
 */
