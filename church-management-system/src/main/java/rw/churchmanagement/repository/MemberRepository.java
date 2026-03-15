package rw.churchmanagement.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import rw.churchmanagement.model.Member;

import java.util.List;

/**
 * Member Repository
 *
 * LOGIC: Data access for Member entity.
 * Demonstrates:
 * - existBy() method
 * - Pagination and Sorting via Pageable
 * - Retrieve members by province through location hierarchy
 *
 * @author Sonia Munezero
 */
@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    /**
     * existBy() demonstration
     * LOGIC: Checks if a membership number already exists before saving.
     */
    boolean existsByMembershipNumber(String membershipNumber);

    /**
     * Find all members of a specific church with pagination
     * LOGIC: One-to-Many - one church has many members
     */
    Page<Member> findByChurchId(Long churchId, Pageable pageable);

    /**
     * Find all members of a specific user
     */
    List<Member> findByUserId(Long userId);

    /**
     * Find members by status with pagination and sorting
     * LOGIC: Pageable handles both pagination and sorting together
     */
    Page<Member> findByStatus(String status, Pageable pageable);

    /**
     * Retrieve members by province code
     * LOGIC: Navigates User → Village → Cell → Sector → District → Province
     * through the self-referenced Location hierarchy
     */
    @Query("SELECT m FROM Member m " +
           "JOIN m.user u " +
           "JOIN u.location l1 " +
           "JOIN l1.parent l2 " +
           "JOIN l2.parent l3 " +
           "JOIN l3.parent l4 " +
           "JOIN l4.parent l5 " +
           "WHERE l5.code = :provinceCode")
    Page<Member> findByProvinceCode(@Param("provinceCode") String provinceCode, Pageable pageable);

    /**
     * Retrieve members by province name
     */
    @Query("SELECT m FROM Member m " +
           "JOIN m.user u " +
           "JOIN u.location l1 " +
           "JOIN l1.parent l2 " +
           "JOIN l2.parent l3 " +
           "JOIN l3.parent l4 " +
           "JOIN l4.parent l5 " +
           "WHERE LOWER(l5.name) = LOWER(:provinceName)")
    Page<Member> findByProvinceName(@Param("provinceName") String provinceName, Pageable pageable);
}
