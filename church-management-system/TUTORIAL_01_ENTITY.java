// FILE: src/main/java/rw/churchmanagement/model/Location.java

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
 * STEP 1: CREATE ENTITY
 * 
 * This class represents the 'locations' table in the database
 */

@Entity                          // Tells Spring this is a database table
@Table(name = "locations")       // Table name in database
@Data                            // Lombok: Auto-generates getters, setters, toString, etc.
@NoArgsConstructor              // Lombok: Creates empty constructor
@AllArgsConstructor             // Lombok: Creates constructor with all fields
public class Location {
    
    // PRIMARY KEY - Auto-generated UUID
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(updatable = false, nullable = false)
    private UUID id;
    
    // COLUMNS
    @Column(nullable = false, unique = true)
    private String code;
    
    @Column(nullable = false)
    private String name;
    
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private LocationType locationType;
    
    // SELF-REFERENCE (Parent-Child relationship)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Location parent;
    
    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Location> children = new ArrayList<>();
    
    // ENUM for location types
    public enum LocationType {
        PROVINCE, DISTRICT, SECTOR, CELL, VILLAGE
    }
    
    // CUSTOM CONSTRUCTOR
    public Location(String code, String name, LocationType locationType, Location parent) {
        this.code = code;
        this.name = name;
        this.locationType = locationType;
        this.parent = parent;
    }
    
    // HELPER METHODS
    public void addChild(Location child) {
        children.add(child);
        child.setParent(this);
    }
    
    public void removeChild(Location child) {
        children.remove(child);
        child.setParent(null);
    }
}

/**
 * KEY ANNOTATIONS EXPLAINED:
 * 
 * @Entity - Marks this class as a JPA entity (database table)
 * @Table - Specifies the table name
 * @Id - Marks the primary key
 * @GeneratedValue - Auto-generates the ID value
 * @Column - Defines column properties (nullable, unique, etc.)
 * @ManyToOne - Many locations can have one parent
 * @OneToMany - One location can have many children
 * @Enumerated - Stores enum as string in database
 */
