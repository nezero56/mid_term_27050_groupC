package rw.churchmanagement.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rw.churchmanagement.model.Location;
import rw.churchmanagement.model.User;
import rw.churchmanagement.repository.LocationRepository;
import rw.churchmanagement.repository.UserRepository;

import java.util.List;
import java.util.Optional;

/**
 * User Service
 * 
 * LOGIC: Service layer for User operations including:
 * - Creating users with Location (Village) reference only (auto-links to all higher levels)
 * - Retrieving users by Province (or any administrative level)
 * - Pagination and Sorting
 * - existBy() method demonstration
 * 
 * @author Sonia Munezero
 */
@Service
@Transactional
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private LocationRepository locationRepository;
    
    /**
     * Create a new User
     * 
     * LOGIC: This is the KEY implementation for the assignment:
     * - User only provides location (village) code/name
     * - System automatically links to Cell, Sector, District, and Province
     * - This is possible due to the self-referenced Location hierarchy
     * 
     * @param user User object with location reference
     * @return Saved user
     */
    public User createUser(User user) {
        // Validate that location exists
        if (user.getLocation() == null || user.getLocation().getId() == null) {
            throw new RuntimeException("Location is required");
        }
        
        // Verify the location exists in database
        Location location = locationRepository.findById(user.getLocation().getId())
            .orElseThrow(() -> new RuntimeException("Location not found with id: " + user.getLocation().getId()));
        
        user.setLocation(location);
        
        return userRepository.save(user);
    }
    
    /**
     * Create user by location code
     * 
     * LOGIC: Alternative method where user provides location code instead of id
     * This is more user-friendly and matches the assignment requirement
     * 
     * @param user User object
     * @param locationCode The location code (typically village)
     * @return Saved user
     */
    public User createUserByLocationCode(User user, String locationCode) {
        Location location = locationRepository.findByCode(locationCode)
            .orElseThrow(() -> new RuntimeException("Location not found with code: " + locationCode));
        
        user.setLocation(location);
        return userRepository.save(user);
    }
    
    /**
     * Get all users with Pagination
     * 
     * LOGIC: Pagination is implemented using Spring Data JPA's Pageable
     * - page: 0-indexed page number
     * - size: number of records per page
     * - sort: sorting criteria (e.g., Sort.by("firstName").ascending())
     * 
     * Benefits of Pagination:
     * 1. Improves performance by loading only required data
     * 2. Reduces memory consumption
     * 3. Better user experience for large datasets
     * 
     * @param page Page number (0-indexed)
     * @param size Page size
     * @param sortBy Field to sort by
     * @param sortDir Sort direction (ASC or DESC)
     * @return Page of users
     */
    public Page<User> getAllUsers(int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("DESC") 
            ? Sort.by(sortBy).descending() 
            : Sort.by(sortBy).ascending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        return userRepository.findAll(pageable);
    }
    
    /**
     * Get all users with default sorting (by firstName ascending)
     * 
     * @param page Page number
     * @param size Page size
     * @return Page of users
     */
    public Page<User> getAllUsers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("firstName").ascending());
        return userRepository.findAll(pageable);
    }
    
    /**
     * Get all users with Sorting only (no pagination)
     * 
     * LOGIC: Sorting is implemented using Spring Data JPA's Sort
     * - Sort.by("field") creates ascending sort
     * - Sort.by("field").descending() for descending
     * - Sort.by("field1").ascending().and(Sort.by("field2").descending()) for multi-field
     * 
     * @param sortBy Field to sort by
     * @param sortDir Sort direction
     * @return List of users sorted
     */
    public List<User> getAllUsersSorted(String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("DESC") 
            ? Sort.by(sortBy).descending() 
            : Sort.by(sortBy).ascending();
        
        return userRepository.findAll(sort);
    }
    
    /**
     * Get user by ID
     */
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }
    
    /**
     * UPDATE USER
     * 
     * LOGIC: Demonstrate existBy() method
     * Before updating, we check if user exists
     * 
     * @param id User ID
     * @param updatedUser Updated user data
     * @return Updated user
     */
    public User updateUser(Long id, User updatedUser) {
        // DEMONSTRATION: existBy() method
        // This checks if a user with given email already exists (excluding current user)
        // LOGIC: existBy returns boolean - more efficient than findBy which returns full object
        if (userRepository.existsByEmail(updatedUser.getEmail())) {
            User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
            
            // Only throw error if email is different
            if (!existingUser.getEmail().equals(updatedUser.getEmail())) {
                throw new RuntimeException("Email already exists: " + updatedUser.getEmail());
            }
        }
        
        return userRepository.findById(id)
            .map(user -> {
                user.setFirstName(updatedUser.getFirstName());
                user.setLastName(updatedUser.getLastName());
                user.setEmail(updatedUser.getEmail());
                user.setPhone(updatedUser.getPhone());
                if (updatedUser.getLocation() != null) {
                    Location location = locationRepository.findById(updatedUser.getLocation().getId())
                        .orElseThrow(() -> new RuntimeException("Location not found"));
                    user.setLocation(location);
                }
                return userRepository.save(user);
            })
            .orElseThrow(() -> new RuntimeException("User not found"));
    }
    
    /**
     * Delete user
     */
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
    
    /**
     * CHECK IF USER EXISTS (existBy demonstration)
     * 
     * LOGIC: existBy* methods are provided by Spring Data JPA
     * They return boolean and are optimized to stop at first match
     * 
     * @param email Email to check
     * @return true if user exists
     */
    public boolean checkUserExists(String email) {
        return userRepository.existsByEmail(email);
    }
    
    // ========== RETRIEVE USERS BY LOCATION ==========
    
    /**
     * GET ALL USERS FROM A GIVEN PROVINCE (by code)
     * 
     * LOGIC: This is the KEY FEATURE requested:
     * - We only need province code
     * - Query navigates: User → Village → Cell → Sector → District → Province
     * - Returns paginated results
     * 
     * @param provinceCode Province code (e.g., "01")
     * @param page Page number
     * @param size Page size
     * @return Page of users in that province
     */
    public Page<User> getUsersByProvinceCode(String provinceCode, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return userRepository.findByProvinceCode(provinceCode, pageable);
    }
    
    /**
     * GET ALL USERS FROM A GIVEN PROVINCE (by name)
     * 
     * @param provinceName Province name (e.g., "Kigali City")
     * @param page Page number
     * @param size Page size
     * @return Page of users in that province
     */
    public Page<User> getUsersByProvinceName(String provinceName, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return userRepository.findByProvinceName(provinceName, pageable);
    }
    
    /**
     * GET ALL USERS FROM A GIVEN DISTRICT
     * 
     * @param districtCode District code
     * @param page Page number
     * @param size Page size
     * @return Page of users in that district
     */
    public Page<User> getUsersByDistrictCode(String districtCode, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return userRepository.findByDistrictCode(districtCode, pageable);
    }
    
    /**
     * GET ALL USERS FROM A GIVEN SECTOR
     * 
     * @param sectorCode Sector code
     * @param page Page number
     * @param size Page size
     * @return Page of users in that sector
     */
    public Page<User> getUsersBySectorCode(String sectorCode, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return userRepository.findBySectorCode(sectorCode, pageable);
    }
    
    /**
     * GET ALL USERS FROM A GIVEN CELL
     * 
     * @param cellCode Cell code
     * @param page Page number
     * @param size Page size
     * @return Page of users in that cell
     */
    public Page<User> getUsersByCellCode(String cellCode, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return userRepository.findByCellCode(cellCode, pageable);
    }
}
