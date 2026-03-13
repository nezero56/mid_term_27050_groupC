package rw.churchmanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rw.churchmanagement.model.Role;

import java.util.Optional;

/**
 * Role Repository
 * 
 * LOGIC: Repository for Role entity used in Many-to-Many relationship
 * 
 * @author Sonia Munezero
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    
    /**
     * Find role by name
     */
    Optional<Role> findByName(String name);
    
    /**
     * Check if role exists by name
     */
    boolean existsByName(String name);
}
