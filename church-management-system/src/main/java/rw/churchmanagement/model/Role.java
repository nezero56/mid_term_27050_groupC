package rw.churchmanagement.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

/**
 * Role Entity - For Many-to-Many relationship demonstration
 * 
 * LOGIC:
 * A Role can be assigned to multiple Users (e.g., MEMBER, LEADER, TREASURER)
 * and a User can have multiple Roles.
 * 
 * This is the inverse side of the Many-to-Many relationship with User.
 * The @ManyToMany annotation references the roles Set in the User entity.
 * 
 * @author Sonia Munezero
 */
@Entity
@Table(name = "roles")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Role {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String name;  // e.g., "MEMBER", "LEADER", "TREASURER", "PASTOR"
    
    private String description;
    
    /**
     * Many-to-Many Relationship with User (inverse side)
     * LOGIC: This is the inverse side of the relationship defined in User entity
     * The 'users' set in Role corresponds to the 'roles' set in User
     * Hibernate manages the relationship through the user_roles join table
     */
    @ManyToMany(mappedBy = "roles")
    private Set<User> users = new HashSet<>();
    
    public Role(String name, String description) {
        this.name = name;
        this.description = description;
    }
}
