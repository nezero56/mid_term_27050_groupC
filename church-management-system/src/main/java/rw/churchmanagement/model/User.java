package rw.churchmanagement.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

/**
 * User Entity - Church member/user
 * 
 * LOGIC: 
 * 1. User has Many-to-One relationship with Village (user belongs to one village)
 * 2. User has Many-to-Many relationship with Role (user can have multiple roles)
 * 3. User has One-to-One relationship with UserProfile (each user has one profile)
 * 
 * KEY FEATURE: When creating a User, we only need the Village code/name.
 * Due to the relationships: Village → Cell → Sector → District → Province,
 * we can retrieve users by Province easily.
 * 
 * @author Sonia Munezero
 */
@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String firstName;
    
    @Column(nullable = false)
    private String lastName;
    
    @Column(nullable = false, unique = true)
    private String email;
    
    private String phone;
    
    /**
     * Many-to-One Relationship with Location (Village)
     * LOGIC: Each User belongs to exactly one Location (typically a Village)
     * This is the KEY relationship - we link to the lowest level (Village)
     * and can navigate up to Province through the chain:
     * User → Village → Cell → Sector → District → Province
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id", nullable = false)
    private Location location;
    
    /**
     * Many-to-Many Relationship with Role
     * LOGIC: A user can have multiple roles (e.g., MEMBER, LEADER, TREASURER)
     * and a role can be assigned to multiple users.
     * 
     * The @JoinTable annotation defines the join table name and columns:
     * - name: "user_roles" is the join table
     * - joinColumns: references the user_id in the join table
     * - inverseJoinColumns: references the role_id in the join table
     * 
     * This demonstrates the Many-to-Many relationship requirement.
     */
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "user_roles",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();
    
    /**
     * One-to-One Relationship with UserProfile
     * LOGIC: Each User has exactly one UserProfile
     * The @OneToOne annotation creates a one-to-one relationship
     * mappedBy = "user" means UserProfile owns the relationship
     * (the foreign key is in the user_profile table)
     * 
     * This demonstrates the One-to-One relationship requirement.
     */
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private UserProfile profile;
    
    public User(String firstName, String lastName, String email, String phone, Location location) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.location = location;
    }
    
    /**
     * Helper method to add a role to this user
     */
    public void addRole(Role role) {
        this.roles.add(role);
        role.getUsers().add(this);
    }
    
    /**
     * Helper method to remove a role from this user
     */
    public void removeRole(Role role) {
        this.roles.remove(role);
        role.getUsers().remove(this);
    }
}
