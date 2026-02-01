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

    // --- 1. PUBLIC: GET ALL (Verified Only & Sorted Newest First) ---
    // ⚠️ CRITICAL: Do not use findAll() here!
    public List<Property> getAllProperties() {
        return propertyRepository.findByOwner_IsVerifiedTrueOrderByCreatedAtDesc();
    }

    // --- 2. PUBLIC/OWNER: GET BY ID ---
    public Property getPropertyById(Long id) {
        return propertyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Property not found with id: " + id));
    }

    // --- 3. OWNER DASHBOARD: GET MY PROPERTIES ---
    public List<Property> getPropertiesByOwner(User owner) {
        return propertyRepository.findByOwner(owner);
    }

    // --- 4. OWNER: SAVE/UPDATE PROPERTY ---
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

        propertyRepository.save(property);
    }

    // --- 5. PUBLIC: SEARCH ---
    public Page<Property> searchProperties(String location, Double price, Pageable pageable) {
        if (location == null) location = "";
        if (price == null) price = 1000000.0; // Default high price to include all
        return propertyRepository.findByLocationContainingIgnoreCaseAndPriceLessThanEqual(location, price, pageable);
    }

    // --- 6. OWNER: DELETE ---
    public void deleteProperty(Long id) {
        propertyRepository.deleteById(id);
    }
}