// FILE: src/main/java/rw/churchmanagement/service/LocationService.java

package rw.churchmanagement.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rw.churchmanagement.dto.LocationDTO;
import rw.churchmanagement.model.Location;
import rw.churchmanagement.model.Location.LocationType;
import rw.churchmanagement.repository.LocationRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * STEP 4: CREATE SERVICE
 * 
 * WHAT IS A SERVICE?
 * - Contains business logic
 * - Sits between Controller and Repository
 * - Handles data transformation (DTO to Entity)
 * - Manages transactions
 */

@Service                    // Marks this as a service component
@Transactional             // All methods run in database transactions
public class LocationService {
    
    // DEPENDENCY INJECTION
    // Spring automatically creates and injects the repository
    @Autowired
    private LocationRepository locationRepository;
    
    /**
     * CREATE LOCATION FROM DTO
     * 
     * This method:
     * 1. Takes DTO from controller
     * 2. Converts it to Entity
     * 3. Fetches parent if parentId is provided
     * 4. Saves to database
     * 5. Returns saved entity
     */
    public Location createLocation(LocationDTO dto) {
        // Create new Location entity
        Location location = new Location();
        location.setCode(dto.getCode());
        location.setName(dto.getName());
        location.setLocationType(dto.getLocationType());
        
        // If parent ID is provided, fetch parent from database
        if (dto.getParentId() != null) {
            Location parent = locationRepository.findById(dto.getParentId())
                .orElseThrow(() -> new RuntimeException("Parent location not found with id: " + dto.getParentId()));
            location.setParent(parent);
        }
        
        // Save to database and return
        return locationRepository.save(location);
    }
    
    /**
     * SAVE LOCATION (Alternative method)
     */
    public Location saveLocation(Location location) {
        if (location.getParent() != null && location.getParent().getId() != null) {
            Location parent = locationRepository.findById(location.getParent().getId())
                .orElseThrow(() -> new RuntimeException("Parent location not found"));
            location.setParent(parent);
        }
        return locationRepository.save(location);
    }
    
    /**
     * SAVE LOCATION WITH PARENT ID
     */
    public Location saveLocationWithParent(Location location, UUID parentId) {
        Location parent = locationRepository.findById(parentId)
            .orElseThrow(() -> new RuntimeException("Parent location not found"));
        location.setParent(parent);
        return locationRepository.save(location);
    }
    
    /**
     * GET ALL LOCATIONS
     */
    public List<Location> getAllLocations() {
        return locationRepository.findAll();
    }
    
    /**
     * GET LOCATION BY ID
     */
    public Optional<Location> getLocationById(UUID id) {
        return locationRepository.findById(id);
    }
    
    /**
     * GET LOCATION BY CODE
     */
    public Optional<Location> getLocationByCode(String code) {
        return locationRepository.findByCode(code);
    }
    
    /**
     * CHECK IF LOCATION EXISTS
     */
    public boolean locationExists(String code) {
        return locationRepository.existsByCode(code);
    }
    
    /**
     * GET LOCATIONS BY TYPE
     */
    public List<Location> getLocationsByType(LocationType type) {
        return locationRepository.findByLocationType(type);
    }
    
    /**
     * GET CHILD LOCATIONS
     */
    public List<Location> getChildLocations(UUID parentId) {
        Location parent = locationRepository.findById(parentId)
            .orElseThrow(() -> new RuntimeException("Parent location not found"));
        return locationRepository.findByParent(parent);
    }
    
    /**
     * GET LOCATIONS BY TYPE AND PARENT
     */
    public List<Location> getLocationsByTypeAndParent(LocationType type, UUID parentId) {
        Location parent = locationRepository.findById(parentId)
            .orElseThrow(() -> new RuntimeException("Parent location not found"));
        return locationRepository.findByLocationTypeAndParent(type, parent);
    }
    
    /**
     * GET ALL PROVINCES
     */
    public List<Location> getProvinces() {
        return locationRepository.findByLocationTypeAndParentIsNull(LocationType.PROVINCE);
    }
    
    /**
     * SAVE COMPLETE HIERARCHY
     */
    public Location saveCompleteHierarchy(Location location) {
        return locationRepository.save(location);
    }
}

/**
 * KEY CONCEPTS:
 * 
 * @Service - Marks this as a Spring service component
 * @Transactional - Ensures database operations are atomic
 * @Autowired - Tells Spring to inject dependencies
 * 
 * Optional<T> - Java wrapper that may or may not contain a value
 *               Prevents NullPointerException
 * 
 * orElseThrow() - If Optional is empty, throw exception
 */
