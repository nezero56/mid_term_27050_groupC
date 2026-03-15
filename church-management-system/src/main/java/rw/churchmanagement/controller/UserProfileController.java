package rw.churchmanagement.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rw.churchmanagement.model.Location;
import rw.churchmanagement.model.User;
import rw.churchmanagement.model.UserProfile;
import rw.churchmanagement.repository.LocationRepository;
import rw.churchmanagement.repository.UserProfileRepository;
import rw.churchmanagement.repository.UserRepository;

import java.util.List;
import java.util.UUID;

/**
 * UserProfile REST Controller
 *
 * Demonstrates all 4 HTTP methods (POST, GET, PUT, DELETE)
 * for the One-to-One relationship between User and UserProfile.
 *
 * @author Sonia Munezero
 */
@RestController
@RequestMapping("/api/profiles")
public class UserProfileController {

    @Autowired
    private UserProfileRepository userProfileRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LocationRepository locationRepository;

    /**
     * POST - Create a new UserProfile
     * LOGIC: Links a profile to an existing User (One-to-One).
     * Optionally links to a Location (village level).
     *
     * Example body:
     * {
     *   "gender": "Male",
     *   "dateOfBirth": "1995-01-15",
     *   "address": "KG 123 St",
     *   "occupation": "Engineer",
     *   "emergencyContact": "+250788000000",
     *   "user": { "id": 1 },
     *   "location": { "id": "uuid-here" }
     * }
     */
    @PostMapping
    public ResponseEntity<UserProfile> createProfile(@RequestBody UserProfile profile) {
        if (profile.getUser() == null || profile.getUser().getId() == null) {
            return ResponseEntity.badRequest().build();
        }

        User user = userRepository.findById(profile.getUser().getId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        profile.setUser(user);

        if (profile.getLocation() != null && profile.getLocation().getId() != null) {
            Location location = locationRepository.findById(profile.getLocation().getId())
                    .orElseThrow(() -> new RuntimeException("Location not found"));
            profile.setLocation(location);
        }

        UserProfile saved = userProfileRepository.save(profile);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    /**
     * GET - Retrieve all UserProfiles
     * LOGIC: Returns all profiles stored in the user_profiles table.
     */
    @GetMapping
    public ResponseEntity<List<UserProfile>> getAllProfiles() {
        return ResponseEntity.ok(userProfileRepository.findAll());
    }

    /**
     * GET - Retrieve a single UserProfile by ID
     * LOGIC: Looks up by primary key in user_profiles table.
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserProfile> getProfileById(@PathVariable Long id) {
        return userProfileRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * GET - Retrieve UserProfile by User ID
     * LOGIC: Uses the One-to-One FK (user_id) to find the profile for a given user.
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<UserProfile> getProfileByUserId(@PathVariable Long userId) {
        return userProfileRepository.findByUserId(userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * PUT - Update an existing UserProfile
     * LOGIC: Finds the profile by ID, updates all fields, saves back.
     * Location can also be updated (re-linked to a different village).
     */
    @PutMapping("/{id}")
    public ResponseEntity<UserProfile> updateProfile(
            @PathVariable Long id,
            @RequestBody UserProfile updatedProfile) {

        return userProfileRepository.findById(id)
                .map(profile -> {
                    profile.setGender(updatedProfile.getGender());
                    profile.setDateOfBirth(updatedProfile.getDateOfBirth());
                    profile.setAddress(updatedProfile.getAddress());
                    profile.setOccupation(updatedProfile.getOccupation());
                    profile.setEmergencyContact(updatedProfile.getEmergencyContact());

                    if (updatedProfile.getLocation() != null && updatedProfile.getLocation().getId() != null) {
                        Location location = locationRepository.findById(updatedProfile.getLocation().getId())
                                .orElseThrow(() -> new RuntimeException("Location not found"));
                        profile.setLocation(location);
                    }

                    return ResponseEntity.ok(userProfileRepository.save(profile));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * DELETE - Delete a UserProfile by ID
     * LOGIC: Removes the profile record; the User itself is NOT deleted
     * because cascade is only on the User side (CascadeType.ALL from User → Profile).
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProfile(@PathVariable Long id) {
        if (!userProfileRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        userProfileRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
