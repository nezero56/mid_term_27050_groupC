package rw.churchmanagement.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rw.churchmanagement.model.User;
import rw.churchmanagement.service.UserService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User REST Controller
 * 
 * LOGIC: REST endpoints for User operations including:
 * - Create user with Village reference only
 * - Pagination and Sorting
 * - Retrieve users by Province (or any administrative level)
 * 
 * @author Sonia Munezero
 */
@RestController
@RequestMapping("/api/users")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    /**
     * Create a new User
     * 
     * LOGIC: The KEY endpoint for the assignment:
     * - User provides location ID (typically village)
     * - System automatically links to Cell, Sector, District, and Province through parent hierarchy
     * 
     * @param user User object with location reference
     * @return Created user
     */
    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        User savedUser = userService.createUser(user);
        return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
    }
    
    /**
     * Create user by location code
     * 
     * LOGIC: More user-friendly endpoint - user provides location code (typically village)
     * 
     * @param user User data
     * @param locationCode Location code (typically village)
     * @return Created user
     */
    @PostMapping("/by-location/{locationCode}")
    public ResponseEntity<User> createUserByLocationCode(
            @RequestBody User user, 
            @PathVariable String locationCode) {
        User savedUser = userService.createUserByLocationCode(user, locationCode);
        return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
    }
    
    /**
     * Get all users with Pagination and Sorting
     * 
     * LOGIC: Demonstrates Pagination and Sorting:
     * - page: page number (0-indexed)
     * - size: records per page
     * - sortBy: field to sort by (default: firstName)
     * - sortDir: sort direction (ASC or DESC)
     * 
     * Example: /api/users?page=0&size=10&sortBy=firstName&sortDir=ASC
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
     * Get all users with Sorting only (no pagination)
     * 
     * @param sortBy Field to sort by
     * @param sortDir Sort direction
     * @return List of users sorted
     */
    @GetMapping("/sorted")
    public ResponseEntity<List<User>> getAllUsersSorted(
            @RequestParam(defaultValue = "firstName") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortDir) {
        
        List<User> users = userService.getAllUsersSorted(sortBy, sortDir);
        return ResponseEntity.ok(users);
    }
    
    /**
     * Get user by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return userService.getUserById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Update user
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
     * Delete user
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
    
    /**
     * Check if user exists (existBy demonstration)
     * 
     * LOGIC: Demonstrates existBy() method usage
     * Returns boolean indicating whether user with given email exists
     */
    @GetMapping("/check-exists")
    public ResponseEntity<Map<String, Boolean>> checkUserExists(
            @RequestParam String email) {
        boolean exists = userService.checkUserExists(email);
        Map<String, Boolean> response = new HashMap<>();
        response.put("exists", exists);
        return ResponseEntity.ok(response);
    }
    
    // ========== RETRIEVE USERS BY LOCATION ==========
    
    /**
     * GET ALL USERS FROM A GIVEN PROVINCE (by code)
     * 
     * LOGIC: KEY FEATURE - Retrieve users by province code
     * This navigates: User → Location (Village) → parent → parent → parent → Location (Province)
     * 
     * Example: /api/users/province/code/01
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
    
    /**
     * GET ALL USERS FROM A GIVEN PROVINCE (by name)
     * 
     * Example: /api/users/province/name/Kigali City
     */
    @GetMapping("/province/name/{provinceName}")
    public ResponseEntity<Map<String, Object>> getUsersByProvinceName(
            @PathVariable String provinceName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Page<User> userPage = userService.getUsersByProvinceName(provinceName, page, size);
        
        Map<String, Object> response = new HashMap<>();
        response.put("users", userPage.getContent());
        response.put("provinceName", provinceName);
        response.put("currentPage", userPage.getNumber());
        response.put("totalItems", userPage.getTotalElements());
        response.put("totalPages", userPage.getTotalPages());
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * GET ALL USERS FROM A GIVEN DISTRICT
     */
    @GetMapping("/district/{districtCode}")
    public ResponseEntity<Map<String, Object>> getUsersByDistrictCode(
            @PathVariable String districtCode,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Page<User> userPage = userService.getUsersByDistrictCode(districtCode, page, size);
        
        Map<String, Object> response = new HashMap<>();
        response.put("users", userPage.getContent());
        response.put("districtCode", districtCode);
        response.put("currentPage", userPage.getNumber());
        response.put("totalItems", userPage.getTotalElements());
        response.put("totalPages", userPage.getTotalPages());
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * GET ALL USERS FROM A GIVEN SECTOR
     */
    @GetMapping("/sector/{sectorCode}")
    public ResponseEntity<Map<String, Object>> getUsersBySectorCode(
            @PathVariable String sectorCode,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Page<User> userPage = userService.getUsersBySectorCode(sectorCode, page, size);
        
        Map<String, Object> response = new HashMap<>();
        response.put("users", userPage.getContent());
        response.put("sectorCode", sectorCode);
        response.put("currentPage", userPage.getNumber());
        response.put("totalItems", userPage.getTotalElements());
        response.put("totalPages", userPage.getTotalPages());
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * GET ALL USERS FROM A GIVEN CELL
     */
    @GetMapping("/cell/{cellCode}")
    public ResponseEntity<Map<String, Object>> getUsersByCellCode(
            @PathVariable String cellCode,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Page<User> userPage = userService.getUsersByCellCode(cellCode, page, size);
        
        Map<String, Object> response = new HashMap<>();
        response.put("users", userPage.getContent());
        response.put("cellCode", cellCode);
        response.put("currentPage", userPage.getNumber());
        response.put("totalItems", userPage.getTotalElements());
        response.put("totalPages", userPage.getTotalPages());
        
        return ResponseEntity.ok(response);
    }
}
