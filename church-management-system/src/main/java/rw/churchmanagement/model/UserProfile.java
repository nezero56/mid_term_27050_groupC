package rw.churchmanagement.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * UserProfile Entity - For One-to-One relationship demonstration
 * 
 * LOGIC:
 * Each User has exactly one UserProfile (biographical info, address details, etc.)
 * The UserProfile is the owning side of the One-to-One relationship,
 * containing the foreign key (user_id) that references the users table.
 * 
 * UserProfile now references Location (Village) instead of separate Village entity.
 * 
 * @author Sonia Munezero
 */
@Entity
@Table(name = "user_profiles")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserProfile {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String gender;
    
    private String dateOfBirth;
    
    private String address;
    
    private String occupation;
    
    private String emergencyContact;
    
    /**
     * Many-to-One Relationship with Location (Village)
     * LOGIC: Each UserProfile belongs to a Location (typically a Village)
     * Through the self-referenced Location hierarchy, we can access:
     * Village → Cell → Sector → District → Province
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id")
    private Location location;
    
    /**
     * One-to-One Relationship with User
     * LOGIC: Each UserProfile belongs to exactly one User
     * 
     * The @JoinColumn annotation specifies:
     * - name: "user_id" - the foreign key column in user_profiles table
     * - nullable: false - each profile must belong to a user
     * 
     * This is the owning side of the One-to-One relationship.
     * The non-owning side (User.profile) has mappedBy = "user"
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    public UserProfile(String gender, String dateOfBirth, String address, 
                      String occupation, String emergencyContact, Location location, User user) {
        this.gender = gender;
        this.dateOfBirth = dateOfBirth;
        this.address = address;
        this.occupation = occupation;
        this.emergencyContact = emergencyContact;
        this.location = location;
        this.user = user;
    }
}
