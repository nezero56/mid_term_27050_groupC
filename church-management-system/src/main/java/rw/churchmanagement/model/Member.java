package rw.churchmanagement.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Member Entity - Links a User to a Church
 *
 * RELATIONSHIPS:
 * 1. Many-to-One with Church → Many members belong to one church (One-to-Many inverse)
 * 2. Many-to-One with User   → Each member record references one user
 *
 * This table demonstrates the One-to-Many relationship:
 * One Church → Many Members
 *
 * @author Sonia Munezero
 */
@Entity
@Table(name = "members")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String membershipNumber;

    @Column(nullable = false)
    private String joinedDate;

    private String status; // ACTIVE, INACTIVE, TRANSFERRED

    /**
     * Many-to-One with Church
     * LOGIC: Many members belong to one church.
     * The foreign key church_id is stored in this (members) table.
     * This is the owning side of the One-to-Many relationship.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "church_id", nullable = false)
    private Church church;

    /**
     * Many-to-One with User
     * LOGIC: Each membership record belongs to one user.
     * A user can be a member of one church at a time.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public Member(String membershipNumber, String joinedDate, String status, Church church, User user) {
        this.membershipNumber = membershipNumber;
        this.joinedDate = joinedDate;
        this.status = status;
        this.church = church;
        this.user = user;
    }
}
