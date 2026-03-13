package rw.churchmanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rw.churchmanagement.model.UserProfile;

import java.util.Optional;

/**
 * UserProfile Repository
 * 
 * LOGIC: Repository for UserProfile entity (One-to-One with User)
 * 
 * @author Sonia Munezero
 */
@Repository
public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {
    
    /**
     * Find profile by user
     */
    Optional<UserProfile> findByUserId(Long userId);
}
