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

@Service
@Transactional
public class LocationService {
    
    @Autowired
    private LocationRepository locationRepository;
    
    public Location createLocation(LocationDTO dto) {
        Location location = new Location();
        location.setCode(dto.getCode());
        location.setName(dto.getName());
        location.setLocationType(dto.getLocationType());
        
        if (dto.getParentId() != null) {
            Location parent = locationRepository.findById(dto.getParentId())
                .orElseThrow(() -> new RuntimeException("Parent location not found with id: " + dto.getParentId()));
            location.setParent(parent);
        }
        
        return locationRepository.save(location);
    }
    
    public Location saveLocation(Location location) {
        if (location.getParent() != null && location.getParent().getId() != null) {
            Location parent = locationRepository.findById(location.getParent().getId())
                .orElseThrow(() -> new RuntimeException("Parent location not found with id: " + location.getParent().getId()));
            location.setParent(parent);
        }
        return locationRepository.save(location);
    }
    
    public Location saveLocationWithParent(Location location, UUID parentId) {
        Location parent = locationRepository.findById(parentId)
            .orElseThrow(() -> new RuntimeException("Parent location not found"));
        location.setParent(parent);
        return locationRepository.save(location);
    }
    
    public List<Location> getAllLocations() {
        return locationRepository.findAll();
    }
    
    public Optional<Location> getLocationById(UUID id) {
        return locationRepository.findById(id);
    }
    
    public Optional<Location> getLocationByCode(String code) {
        return locationRepository.findByCode(code);
    }
    
    public boolean locationExists(String code) {
        return locationRepository.existsByCode(code);
    }
    
    public List<Location> getLocationsByType(LocationType type) {
        return locationRepository.findByLocationType(type);
    }
    
    public List<Location> getChildLocations(UUID parentId) {
        Location parent = locationRepository.findById(parentId)
            .orElseThrow(() -> new RuntimeException("Parent location not found"));
        return locationRepository.findByParent(parent);
    }
    
    public List<Location> getLocationsByTypeAndParent(LocationType type, UUID parentId) {
        Location parent = locationRepository.findById(parentId)
            .orElseThrow(() -> new RuntimeException("Parent location not found"));
        return locationRepository.findByLocationTypeAndParent(type, parent);
    }
    
    public List<Location> getProvinces() {
        return locationRepository.findByLocationTypeAndParentIsNull(LocationType.PROVINCE);
    }
    
    public Location saveCompleteHierarchy(Location location) {
        return locationRepository.save(location);
    }

    /**
     * Update an existing Location
     * LOGIC: Find by ID, update fields, save back
     */
    public Location updateLocation(UUID id, LocationDTO dto) {
        Location location = locationRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Location not found with id: " + id));

        location.setCode(dto.getCode());
        location.setName(dto.getName());
        location.setLocationType(dto.getLocationType());

        if (dto.getParentId() != null) {
            Location parent = locationRepository.findById(dto.getParentId())
                .orElseThrow(() -> new RuntimeException("Parent location not found with id: " + dto.getParentId()));
            location.setParent(parent);
        } else {
            location.setParent(null);
        }

        return locationRepository.save(location);
    }

    /**
     * Delete a Location by ID
     * LOGIC: Check existence first using existsById, then delete
     */
    public void deleteLocation(UUID id) {
        if (!locationRepository.existsById(id)) {
            throw new RuntimeException("Location not found with id: " + id);
        }
        locationRepository.deleteById(id);
    }
}
