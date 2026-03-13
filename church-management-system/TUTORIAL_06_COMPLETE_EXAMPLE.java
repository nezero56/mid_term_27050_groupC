/**
 * COMPLETE EXAMPLE: Creating User APIs from Scratch
 * 
 * This shows all 5 steps together for User management
 */

// ============================================
// STEP 1: CREATE USER ENTITY
// ============================================
// FILE: src/main/java/rw/churchmanagement/model/User.java

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
    
    // Relationship with Location
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id", nullable = false)
    private Location location;
    
    // Relationship with Roles (Many-to-Many)
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "user_roles",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();
    
    // Relationship with UserProfile (One-to-One)
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private UserProfile profile;
}

// ============================================
// STEP 2: CREATE USER REPOSITORY
// ============================================
// FILE: src/main/java/rw/churchmanagement/repository/UserRepository.java

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    // Find by email
    Optional<User> findByEmail(String email);
    
    // Check if exists
    boolean existsByEmail(String email);
    
    // Pagination support
    Page<User> findAll(Pageable pageable);
    
    // Sorting support
    List<User> findAll(Sort sort);
    
    // Custom query to find users by province code
    @Query("SELECT u FROM User u JOIN u.location l1 JOIN l1.parent l2 JOIN l2.parent l3 JOIN l3.parent l4 JOIN l4.parent l5 WHERE l5.code = :provinceCode")
    Page<User> findByProvinceCode(@Param("provinceCode") String provinceCode, Pageable pageable);
}

// ============================================
// STEP 3: CREATE USER SERVICE
// ============================================
// FILE: src/main/java/rw/churchmanagement/service/UserService.java

@Service
@Transactional
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private LocationRepository locationRepository;
    
    // Create user
    public User createUser(User user) {
        if (user.getLocation() == null || user.getLocation().getId() == null) {
            throw new RuntimeException("Location is required");
        }
        
        Location location = locationRepository.findById(user.getLocation().getId())
            .orElseThrow(() -> new RuntimeException("Location not found"));
        
        user.setLocation(location);
        return userRepository.save(user);
    }
    
    // Create user by location code
    public User createUserByLocationCode(User user, String locationCode) {
        Location location = locationRepository.findByCode(locationCode)
            .orElseThrow(() -> new RuntimeException("Location not found with code: " + locationCode));
        
        user.setLocation(location);
        return userRepository.save(user);
    }
    
    // Get all users with pagination
    public Page<User> getAllUsers(int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("DESC") 
            ? Sort.by(sortBy).descending() 
            : Sort.by(sortBy).ascending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        return userRepository.findAll(pageable);
    }
    
    // Get user by ID
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }
    
    // Update user
    public User updateUser(Long id, User updatedUser) {
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
    
    // Delete user
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
    
    // Check if user exists
    public boolean checkUserExists(String email) {
        return userRepository.existsByEmail(email);
    }
    
    // Get users by province
    public Page<User> getUsersByProvinceCode(String provinceCode, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return userRepository.findByProvinceCode(provinceCode, pageable);
    }
}

// ============================================
// STEP 4: CREATE USER CONTROLLER
// ============================================
// FILE: src/main/java/rw/churchmanagement/controller/UserController.java

@RestController
@RequestMapping("/api/users")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    /**
     * API 1: CREATE USER
     * POST /api/users
     */
    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        User savedUser = userService.createUser(user);
        return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
    }
    
    /**
     * API 2: CREATE USER BY LOCATION CODE
     * POST /api/users/by-location/{locationCode}
     */
    @PostMapping("/by-location/{locationCode}")
    public ResponseEntity<User> createUserByLocationCode(
            @RequestBody User user, 
            @PathVariable String locationCode) {
        User savedUser = userService.createUserByLocationCode(user, locationCode);
        return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
    }
    
    /**
     * API 3: GET ALL USERS (PAGINATED)
     * GET /api/users?page=0&size=10&sortBy=firstName&sortDir=ASC
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "firstName") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortDir) {
        
        Page<User> userPage = userService.getAllUsers(page, size, sortBy, sortDir);
        
        Map<String, Object> response = new HashMap<>();
        response.put("users", userPage.getContent());
        response.put("currentPage", userPage.getNumber());
        response.put("totalItems", userPage.getTotalElements());
        response.put("totalPages", userPage.getTotalPages());
        response.put("pageSize", userPage.getSize());
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * API 4: GET USER BY ID
     * GET /api/users/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return userService.getUserById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * API 5: UPDATE USER
     * PUT /api/users/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User user) {
        try {
            User updatedUser = userService.updateUser(id, user);
            return ResponseEntity.ok(updatedUser);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * API 6: DELETE USER
     * DELETE /api/users/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
    
    /**
     * API 7: CHECK IF USER EXISTS
     * GET /api/users/check-exists?email=john@example.com
     */
    @GetMapping("/check-exists")
    public ResponseEntity<Map<String, Boolean>> checkUserExists(@RequestParam String email) {
        boolean exists = userService.checkUserExists(email);
        Map<String, Boolean> response = new HashMap<>();
        response.put("exists", exists);
        return ResponseEntity.ok(response);
    }
    
    /**
     * API 8: GET USERS BY PROVINCE
     * GET /api/users/province/code/{provinceCode}?page=0&size=10
     */
    @GetMapping("/province/code/{provinceCode}")
    public ResponseEntity<Map<String, Object>> getUsersByProvinceCode(
            @PathVariable String provinceCode,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Page<User> userPage = userService.getUsersByProvinceCode(provinceCode, page, size);
        
        Map<String, Object> response = new HashMap<>();
        response.put("users", userPage.getContent());
        response.put("provinceCode", provinceCode);
        response.put("currentPage", userPage.getNumber());
        response.put("totalItems", userPage.getTotalElements());
        response.put("totalPages", userPage.getTotalPages());
        
        return ResponseEntity.ok(response);
    }
}

/**
 * SUMMARY: HOW TO CREATE ANY API
 * 
 * 1. CREATE ENTITY (@Entity, @Table, @Id, @Column, relationships)
 * 2. CREATE REPOSITORY (extends JpaRepository, add custom methods)
 * 3. CREATE SERVICE (@Service, business logic, call repository)
 * 4. CREATE CONTROLLER (@RestController, @RequestMapping, HTTP methods)
 * 5. TEST IN POSTMAN
 * 
 * THAT'S IT! Spring Boot does the rest automatically!
 */
