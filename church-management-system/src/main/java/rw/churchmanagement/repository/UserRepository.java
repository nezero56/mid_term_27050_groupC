package rw.churchmanagement.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import rw.churchmanagement.model.User;

import java.util.List;
import java.util.Optional;

/**
 * User Repository
 * 
 * LOGIC: Repository for User entity with important custom queries.
 * 
 * KEY FEATURE: Retrieve all users from a given province using province code OR name.
 * This uses JPQL to navigate the self-referenced Location hierarchy:
 * User → Location (Village) → Location (Cell) → Location (Sector) → Location (District) → Location (Province)
 * 
 * We also demonstrate Sorting and Pagination support.
 * 
 * @author Sonia Munezero
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    /**
     * Find user by email
     */
    Optional<User> findByEmail(String email);
    
    /**
     * Check if user exists by email
     * DEMONSTRATES existBy() method
     */
    boolean existsByEmail(String email);
    
    // ========== PAGINATION AND SORTING METHODS ==========
    
    /**
     * Find all users with pagination
     * LOGIC: Pageable contains page number (0-indexed), page size, and Sort options
     * Example: PageRequest.of(0, 10, Sort.by("firstName").ascending())
     * 
     * @param pageable Contains page number, size, and sorting information
     * @return Page of users
     */
    Page<User> findAll(Pageable pageable);
    
    /**
     * Find users with sorting by first name
     * LOGIC: Sort parameter defines the ordering of results
     * 
     * @param sort Sort configuration (e.g., Sort.by("firstName").ascending())
     * @return List of users sorted
     */
    List<User> findAll(Sort sort);
    
    // ========== CUSTOM QUERY METHODS FOR RETRIEVING BY PROVINCE ==========
    
    /**
     * RETRIEVE ALL USERS FROM A GIVEN PROVINCE (BY CODE)
     * 
     * LOGIC: This query navigates the self-referenced Location hierarchy:
     * User (u) → Location village (l1) → Location cell (l2) → Location sector (l3) → Location district (l4) → Location province (l5)
     * 
     * JPQL Query breakdown:
     * SELECT u FROM User u
     * JOIN u.location l1 (village)
     * JOIN l1.parent l2 (cell)
     * JOIN l2.parent l3 (sector)
     * JOIN l3.parent l4 (district)
     * JOIN l4.parent l5 (province)
     * WHERE l5.code = :provinceCode
     * 
     * This demonstrates how we can retrieve users by Province
     * even though users are only linked to Village!
     * 
     * @param provinceCode The province code (e.g., "01" for Kigali City)
     * @return Page of users in that province
     */
    @Query("SELECT u FROM User u JOIN u.location l1 JOIN l1.parent l2 JOIN l2.parent l3 JOIN l3.parent l4 JOIN l4.parent l5 WHERE l5.code = :provinceCode")
    Page<User> findByProvinceCode(@Param("provinceCode") String provinceCode, Pageable pageable);
    
    /**
     * RETRIEVE ALL USERS FROM A GIVEN PROVINCE (BY NAME)
     * 
     * LOGIC: Same as above but uses province name instead of code
     * 
     * @param provinceName The province name (e.g., "Kigali City")
     * @return Page of users in that province
     */
    @Query("SELECT u FROM User u JOIN u.location l1 JOIN l1.parent l2 JOIN l2.parent l3 JOIN l3.parent l4 JOIN l4.parent l5 WHERE LOWER(l5.name) = LOWER(:provinceName)")
    Page<User> findByProvinceName(@Param("provinceName") String provinceName, Pageable pageable);
    
    /**
     * RETRIEVE ALL USERS FROM A GIVEN DISTRICT (BY CODE)
     * 
     * @param districtCode The district code
     * @return Page of users in that district
     */
    @Query("SELECT u FROM User u JOIN u.location l1 JOIN l1.parent l2 JOIN l2.parent l3 JOIN l3.parent l4 WHERE l4.code = :districtCode")
    Page<User> findByDistrictCode(@Param("districtCode") String districtCode, Pageable pageable);
    
    /**
     * RETRIEVE ALL USERS FROM A GIVEN SECTOR (BY CODE)
     * 
     * @param sectorCode The sector code
     * @return Page of users in that sector
     */
    @Query("SELECT u FROM User u JOIN u.location l1 JOIN l1.parent l2 JOIN l2.parent l3 WHERE l3.code = :sectorCode")
    Page<User> findBySectorCode(@Param("sectorCode") String sectorCode, Pageable pageable);
    
    /**
     * RETRIEVE ALL USERS FROM A GIVEN CELL (BY CODE)
     * 
     * @param cellCode The cell code
     * @return Page of users in that cell
     */
    @Query("SELECT u FROM User u JOIN u.location l1 JOIN l1.parent l2 WHERE l2.code = :cellCode")
    Page<User> findByCellCode(@Param("cellCode") String cellCode, Pageable pageable);
}
