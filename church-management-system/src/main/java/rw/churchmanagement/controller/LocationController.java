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

@RestController
@RequestMapping("/api/locations")
public class LocationController {
    
    @Autowired
    private LocationService locationService;
    
    @PostMapping
    public ResponseEntity<Location> createLocation(@RequestBody LocationDTO dto) {
        Location saved = locationService.createLocation(dto);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }
    
    @GetMapping
    public ResponseEntity<List<Location>> getAllLocations() {
        return ResponseEntity.ok(locationService.getAllLocations());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Location> getLocationById(@PathVariable UUID id) {
        return locationService.getLocationById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/code/{code}")
    public ResponseEntity<Location> getLocationByCode(@PathVariable String code) {
        return locationService.getLocationByCode(code)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping("/with-parent")
    public ResponseEntity<Location> createLocationWithParent(
            @RequestBody Location location,
            @RequestParam UUID parentId) {
        Location saved = locationService.saveLocationWithParent(location, parentId);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }
    
    @GetMapping("/check/{code}")
    public ResponseEntity<Boolean> checkLocationExists(@PathVariable String code) {
        return ResponseEntity.ok(locationService.locationExists(code));
    }
    
    @GetMapping("/type/{type}")
    public ResponseEntity<List<Location>> getLocationsByType(@PathVariable LocationType type) {
        return ResponseEntity.ok(locationService.getLocationsByType(type));
    }
    
    @GetMapping("/provinces")
    public ResponseEntity<List<Location>> getProvinces() {
        return ResponseEntity.ok(locationService.getProvinces());
    }
    
    @GetMapping("/{parentId}/children")
    public ResponseEntity<List<Location>> getChildLocations(@PathVariable UUID parentId) {
        return ResponseEntity.ok(locationService.getChildLocations(parentId));
    }
    
    @GetMapping("/{parentId}/children/type/{type}")
    public ResponseEntity<List<Location>> getLocationsByTypeAndParent(
            @PathVariable UUID parentId,
            @PathVariable LocationType type) {
        return ResponseEntity.ok(locationService.getLocationsByTypeAndParent(type, parentId));
    }

    /**
     * UPDATE a Location
     * PUT /api/locations/{id}
     * LOGIC: Updates code, name, type, and optionally re-parents the location
     */
    @PutMapping("/{id}")
    public ResponseEntity<Location> updateLocation(
            @PathVariable UUID id,
            @RequestBody LocationDTO dto) {
        try {
            Location updated = locationService.updateLocation(id, dto);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * DELETE a Location
     * DELETE /api/locations/{id}
     * LOGIC: Removes the location; cascades to children due to orphanRemoval = true
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLocation(@PathVariable UUID id) {
        try {
            locationService.deleteLocation(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
