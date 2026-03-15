package rw.churchmanagement.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Church Entity - 5th table required by ERD
 *
 * RELATIONSHIPS:
 * 1. One-to-One  with Location  → Each church is located at one village
 * 2. One-to-Many with Member    → One church has many members
 *
 * @author Sonia Munezero
 */
@Entity
@Table(name = "churches")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Church {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private String denomination;

    private String phoneNumber;

    private String email;

    private String foundedYear;

    /**
     * One-to-One Relationship with Location (Village)
     * LOGIC: Each church is located at exactly one village.
     * The foreign key location_id sits in the churches table (owning side).
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id", nullable = false)
    private Location location;

    /**
     * One-to-Many Relationship with Member
     * LOGIC: One church has many members.
     * The foreign key church_id sits in the members table.
     * mappedBy = "church" refers to the church field in Member entity.
     */
    @OneToMany(mappedBy = "church", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Member> members = new ArrayList<>();

    public Church(String name, String denomination, String phoneNumber, String email, String foundedYear, Location location) {
        this.name = name;
        this.denomination = denomination;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.foundedYear = foundedYear;
        this.location = location;
    }
}
