package com.horizonix.greennest.service;

import com.horizonix.greennest.entity.Property;
import com.horizonix.greennest.entity.User;
import com.horizonix.greennest.repository.PropertyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
public class PropertyService {

    private static final Logger logger = LoggerFactory.getLogger(PropertyService.class);

    @Autowired
    private PropertyRepository propertyRepository;

    // Try uploads in multiple locations (for development and production)
    private final String[] uploadDirs = {
        "src/main/resources/static/uploads/",
        "target/classes/static/uploads/",
        System.getProperty("java.io.tmpdir") + "/greennest/uploads/"
    };

    // --- 1. PUBLIC: GET ALL (Only "APPROVED" listings show up) ---
    public List<Property> getAllProperties() {
        return propertyRepository.findByStatusOrderByCreatedAtDesc("APPROVED");
    }

    // --- 2. ADMIN: GET PENDING (For Admin Dashboard) ---
    public List<Property> getPendingProperties() {
        return propertyRepository.findByStatusOrderByCreatedAtDesc("PENDING");
    }

    // --- 3. ADMIN: APPROVE PROPERTY ---
    public void approveProperty(Long id) {
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
        Path uploadPath = null;

        // Try to find or create an upload directory
        for (String dir : uploadDirs) {
            Path path = Paths.get(dir).toAbsolutePath();
            try {
                if (!Files.exists(path)) {
                    Files.createDirectories(path);
                }
                uploadPath = path;
                logger.info("✅ Using upload directory: {}", path);
                break;
            } catch (IOException e) {
                logger.warn("⚠️ Could not create directory: {}", dir);
                continue;
            }
        }

        if (uploadPath == null) {
            throw new IOException("Could not create any upload directories");
        }

        // Handle Image Upload
        if (image != null && !image.isEmpty()) {
            String fileName = UUID.randomUUID().toString() + "_" + image.getOriginalFilename();
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(image.getInputStream(), filePath);
            property.setImageName(fileName);

            logger.info("✅ Image uploaded successfully: {}", filePath.toAbsolutePath());
        } else {
            logger.warn("⚠️ No image provided for property: {}", property.getTitle());
        }

        // Always set to PENDING when saving/updating
        property.setStatus("PENDING");

        propertyRepository.save(property);
    }

    // --- 7. PUBLIC: SEARCH (Only search APPROVED items) ---
    public Page<Property> searchProperties(String location, Double price, Pageable pageable) {
        if (location == null) location = "";
        if (price == null) price = 1000000.0;

        return propertyRepository.findByLocationContainingIgnoreCaseAndPriceLessThanEqualAndStatus(
                location, price, "APPROVED", pageable
        );
    }

    // --- 8. OWNER: DELETE ---
    public void deleteProperty(Long id) {
        propertyRepository.deleteById(id);
    }
}