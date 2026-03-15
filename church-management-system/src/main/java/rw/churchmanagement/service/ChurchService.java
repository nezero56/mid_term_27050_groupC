package rw.churchmanagement.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rw.churchmanagement.model.Church;
import rw.churchmanagement.model.Location;
import rw.churchmanagement.repository.ChurchRepository;
import rw.churchmanagement.repository.LocationRepository;

import java.util.List;
import java.util.Optional;

/**
 * Church Service
 *
 * LOGIC: Business logic for Church operations.
 * Uses existsByName() before saving to prevent duplicates.
 * Uses Pageable for pagination and sorting.
 *
 * @author Sonia Munezero
 */
@Service
@Transactional
public class ChurchService {

    @Autowired
    private ChurchRepository churchRepository;

    @Autowired
    private LocationRepository locationRepository;

    /**
     * Create a new Church
     * LOGIC: Uses existsByName() to check for duplicates before saving.
     */
    public Church createChurch(Church church) {
        if (churchRepository.existsByName(church.getName())) {
            throw new RuntimeException("Church with name '" + church.getName() + "' already exists");
        }
        if (church.getLocation() != null && church.getLocation().getId() != null) {
            Location location = locationRepository.findById(church.getLocation().getId())
                    .orElseThrow(() -> new RuntimeException("Location not found"));
            church.setLocation(location);
        }
        return churchRepository.save(church);
    }

    /**
     * Get all churches with Pagination and Sorting
     * LOGIC: PageRequest.of(page, size, sort) combines both pagination and sorting.
     */
    public Page<Church> getAllChurches(int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("DESC")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return churchRepository.findAll(pageable);
    }

    public Optional<Church> getChurchById(Long id) {
        return churchRepository.findById(id);
    }

    public Church updateChurch(Long id, Church updated) {
        return churchRepository.findById(id).map(church -> {
            church.setName(updated.getName());
            church.setDenomination(updated.getDenomination());
            church.setPhoneNumber(updated.getPhoneNumber());
            church.setEmail(updated.getEmail());
            church.setFoundedYear(updated.getFoundedYear());
            if (updated.getLocation() != null && updated.getLocation().getId() != null) {
                Location location = locationRepository.findById(updated.getLocation().getId())
                        .orElseThrow(() -> new RuntimeException("Location not found"));
                church.setLocation(location);
            }
            return churchRepository.save(church);
        }).orElseThrow(() -> new RuntimeException("Church not found"));
    }

    public void deleteChurch(Long id) {
        if (!churchRepository.existsById(id)) {
            throw new RuntimeException("Church not found");
        }
        churchRepository.deleteById(id);
    }

    public boolean churchExists(String name) {
        return churchRepository.existsByName(name);
    }

    public List<Church> getAllChurches() {
        return churchRepository.findAll(Sort.by("name").ascending());
    }
}
