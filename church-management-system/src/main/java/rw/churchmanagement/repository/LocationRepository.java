package rw.churchmanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rw.churchmanagement.model.Location;
import rw.churchmanagement.model.Location.LocationType;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface LocationRepository extends JpaRepository<Location, UUID> {
    
    Optional<Location> findByCode(String code);
    
    boolean existsByCode(String code);
    
    List<Location> findByLocationType(LocationType locationType);
    
    List<Location> findByParent(Location parent);
    
    List<Location> findByLocationTypeAndParent(LocationType locationType, Location parent);
    
    List<Location> findByLocationTypeAndParentIsNull(LocationType locationType);
}
