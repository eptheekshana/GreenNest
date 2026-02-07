package com.horizonix.greennest.service;

import com.horizonix.greennest.entity.Property;
import com.horizonix.greennest.entity.User;
import com.horizonix.greennest.repository.PropertyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
public class PropertyService {

    @Autowired
    private PropertyRepository propertyRepository;

    private final String uploadDir = "src/main/resources/static/uploads/";

    // --- 1. PUBLIC: GET ALL (Only "APPROVED" listings show up) ---
    public List<Property> getAllProperties() {
        // Now we fetch by STATUS = "APPROVED"
        return propertyRepository.findByStatusOrderByCreatedAtDesc("APPROVED");
    }

    // --- 2. ADMIN: GET PENDING (For Admin Dashboard) ---
    public List<Property> getPendingProperties() {
        // Fetch items waiting for approval
        return propertyRepository.findByStatus("PENDING");
    }

    // --- 3. ADMIN: APPROVE PROPERTY ---
    public void approveProperty(Long id) {
        // Admin clicks approve -> Status becomes APPROVED
        Property property = propertyRepository.findById(id).orElse(null);
        if (property != null) {
            property.setStatus("APPROVED");
            propertyRepository.save(property);
        }
    }

    // --- 4. PUBLIC/OWNER: GET BY ID ---
    public Property getPropertyById(Long id) {
        return propertyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Property not found with id: " + id));
    }

    // --- 5. OWNER DASHBOARD: GET MY PROPERTIES ---
    public List<Property> getPropertiesByOwner(User owner) {
        return propertyRepository.findByOwner(owner);
    }

    // --- 6. OWNER: SAVE/UPDATE PROPERTY ---
    public void saveProperty(Property property, MultipartFile image) throws IOException {
        // Ensure upload directory exists
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Handle Image Upload
        if (image != null && !image.isEmpty()) {
            String fileName = UUID.randomUUID().toString() + "_" + image.getOriginalFilename();
            Path path = Paths.get(uploadDir + fileName);
            Files.copy(image.getInputStream(), path);
            property.setImageName(fileName);
        }

        // Always set to PENDING when saving/updating
        // This ensures modified or new listings must be re-approved
        property.setStatus("PENDING");

        propertyRepository.save(property);
    }

    // --- 7. PUBLIC: SEARCH (Only search APPROVED items) ---
    public Page<Property> searchProperties(String location, Double price, Pageable pageable) {
        if (location == null) location = "";
        if (price == null) price = 1000000.0; // Default high price

        // Added "APPROVED" to filter so hidden items don't appear in search results
        return propertyRepository.findByLocationContainingIgnoreCaseAndPriceLessThanEqualAndStatus(
                location, price, "APPROVED", pageable
        );
    }

    // --- 8. OWNER: DELETE ---
    public void deleteProperty(Long id) {
        propertyRepository.deleteById(id);
    }
}