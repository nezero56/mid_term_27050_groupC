package rw.churchmanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rw.churchmanagement.model.Church;

import java.util.Optional;

/**
 * Church Repository
 *
 * LOGIC: Data access for Church entity.
 * existsByName() demonstrates the existBy() method requirement.
 *
 * @author Sonia Munezero
 */
@Repository
public interface ChurchRepository extends JpaRepository<Church, Long> {

    Optional<Church> findByName(String name);

    /**
     * existBy() demonstration
     * LOGIC: Returns true/false without loading the full object.
     * Used before saving to prevent duplicate church names.
     */
    boolean existsByName(String name);
}
