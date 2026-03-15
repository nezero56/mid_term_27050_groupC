package rw.churchmanagement.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rw.churchmanagement.model.Role;
import rw.churchmanagement.model.User;
import rw.churchmanagement.repository.RoleRepository;
import rw.churchmanagement.repository.UserRepository;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Role REST Controller
 *
 * Demonstrates Many-to-Many relationship between User and Role.
 * A User can have multiple Roles, and a Role can belong to multiple Users.
 * The join table is: user_roles (user_id, role_id)
 *
 * @author Sonia Munezero
 */
@RestController
@RequestMapping("/api/roles")
public class RoleController {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * POST - Create a new Role
     * LOGIC: Saves a role (e.g., MEMBER, LEADER, TREASURER, PASTOR) to the roles table.
     *
     * Example body: { "name": "MEMBER", "description": "Church member" }
     */
    @PostMapping
    public ResponseEntity<Role> createRole(@RequestBody Role role) {
        if (roleRepository.existsByName(role.getName())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        return new ResponseEntity<>(roleRepository.save(role), HttpStatus.CREATED);
    }

    /**
     * GET - Retrieve all Roles
     */
    @GetMapping
    public ResponseEntity<List<Role>> getAllRoles() {
        return ResponseEntity.ok(roleRepository.findAll());
    }

    /**
     * GET - Retrieve a Role by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Role> getRoleById(@PathVariable Long id) {
        return roleRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * PUT - Update a Role
     * LOGIC: Finds role by ID, updates name and description, saves back.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Role> updateRole(@PathVariable Long id, @RequestBody Role updatedRole) {
        return roleRepository.findById(id)
                .map(role -> {
                    role.setName(updatedRole.getName());
                    role.setDescription(updatedRole.getDescription());
                    return ResponseEntity.ok(roleRepository.save(role));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * DELETE - Delete a Role
     * LOGIC: Removes role from roles table and from user_roles join table automatically.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRole(@PathVariable Long id) {
        if (!roleRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        roleRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * POST - Assign a Role to a User (Many-to-Many)
     * LOGIC: This inserts a record into the user_roles join table.
     * A user can have multiple roles, and a role can be assigned to multiple users.
     *
     * POST /api/roles/{roleId}/assign/{userId}
     */
    @PostMapping("/{roleId}/assign/{userId}")
    public ResponseEntity<Map<String, String>> assignRoleToUser(
            @PathVariable Long roleId,
            @PathVariable Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Role not found"));

        user.addRole(role);
        userRepository.save(user);

        return ResponseEntity.ok(Map.of(
                "message", "Role '" + role.getName() + "' assigned to user '" + user.getFirstName() + "' successfully"
        ));
    }

    /**
     * DELETE - Remove a Role from a User (Many-to-Many)
     * LOGIC: Removes the record from the user_roles join table.
     *
     * DELETE /api/roles/{roleId}/remove/{userId}
     */
    @DeleteMapping("/{roleId}/remove/{userId}")
    public ResponseEntity<Map<String, String>> removeRoleFromUser(
            @PathVariable Long roleId,
            @PathVariable Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Role not found"));

        user.removeRole(role);
        userRepository.save(user);

        return ResponseEntity.ok(Map.of(
                "message", "Role '" + role.getName() + "' removed from user '" + user.getFirstName() + "' successfully"
        ));
    }

    /**
     * GET - Get all Users assigned to a Role (Many-to-Many)
     * LOGIC: Navigates the inverse side of the Many-to-Many relationship.
     */
    @GetMapping("/{roleId}/users")
    public ResponseEntity<Set<User>> getUsersByRole(@PathVariable Long roleId) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Role not found"));
        return ResponseEntity.ok(role.getUsers());
    }

    /**
     * GET - Check if role exists by name (existBy demonstration)
     * LOGIC: existsByName() returns boolean - efficient existence check.
     */
    @GetMapping("/check-exists")
    public ResponseEntity<Map<String, Boolean>> checkRoleExists(@RequestParam String name) {
        return ResponseEntity.ok(Map.of("exists", roleRepository.existsByName(name)));
    }
}
