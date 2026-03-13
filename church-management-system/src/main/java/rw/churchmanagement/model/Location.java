package rw.churchmanagement.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Location Entity - Self-referenced hierarchical structure for Rwanda administrative divisions
 * 
 * LOGIC: Single table for Province → District → Sector → Cell → Village hierarchy
 * Uses self-referencing to create parent-child relationships
 * Uses UUID for IDs to ensure uniqueness across distributed systems
 * 
 * @author Sonia Munezero
 */
@Entity
@Table(name = "locations")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Location {
    
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(updatable = false, nullable = false)
    private UUID id;
    
    @Column(nullable = false, unique = true)
    private String code;
    
    @Column(nullable = false)
    private String name;
    
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private LocationType locationType;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Location parent;
    
    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Location> children = new ArrayList<>();
    
    public Location(String code, String name, LocationType locationType, Location parent) {
        this.code = code;
        this.name = name;
        this.locationType = locationType;
        this.parent = parent;
    }
    
    public void addChild(Location child) {
        children.add(child);
        child.setParent(this);
    }
    
    public void removeChild(Location child) {
        children.remove(child);
        child.setParent(null);
    }
    
    public enum LocationType {
        PROVINCE, DISTRICT, SECTOR, CELL, VILLAGE
    }
}
