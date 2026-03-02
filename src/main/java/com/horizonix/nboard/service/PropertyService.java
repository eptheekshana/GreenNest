package com.horizonix.nboard.service;

import com.horizonix.nboard.entity.Property;
import com.horizonix.nboard.entity.User;
import com.horizonix.nboard.repository.PropertyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.util.List;

@Service
public class PropertyService {

    private static final Logger logger = LoggerFactory.getLogger(PropertyService.class);

    @Autowired
    private PropertyRepository propertyRepository;

    @Autowired
    private DigitalOceanSpacesService spacesService;

    @Autowired
    private LocalFileStorageService localFileStorageService;

    @Value("${do.spaces.key:YOUR_SPACES_ACCESS_KEY}")
    private String spacesAccessKey;

    @Value("${file.storage.mode:auto}")
    private String storageMode;

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
        // Handle Image Upload
        if (image != null && !image.isEmpty()) {
            try {
                String imageUrl = uploadImage(image);
                property.setImageUrl(imageUrl);
            } catch (IOException e) {
                logger.error("Failed to upload image: {}", e.getMessage());
                throw e;
            }
        } else {
            logger.warn("No image provided for property: {}", property.getTitle());
        }

        // Always set to PENDING when saving/updating
        property.setStatus("PENDING");

        propertyRepository.save(property);
    }

    public void updateProperty(Property existing, Property updated, MultipartFile image) throws IOException {
        existing.setTitle(updated.getTitle());
        existing.setLocation(updated.getLocation());
        existing.setPrice(updated.getPrice());
        existing.setDescription(updated.getDescription());

        if (image != null && !image.isEmpty()) {
            if (existing.getImageUrl() != null) {
                if (existing.getImageUrl().startsWith("/uploads/")) {
                    localFileStorageService.deleteImageLocally(existing.getImageUrl());
                } else {
                    spacesService.deleteImage(existing.getImageUrl());
                }
            }
            String imageUrl = uploadImage(image);
            existing.setImageUrl(imageUrl);
        }

        existing.setStatus("PENDING");
        propertyRepository.save(existing);
    }

    private String uploadImage(MultipartFile image) throws IOException {
        String imageUrl;

        boolean spacesConfigured = spacesAccessKey != null
                && !spacesAccessKey.equals("YOUR_SPACES_ACCESS_KEY")
                && !spacesAccessKey.isEmpty();

        boolean useLocalStorage = storageMode.equals("local")
                || (!spacesConfigured && storageMode.equals("auto"));

        if (useLocalStorage) {
            logger.info("Using local file storage for image upload");
            imageUrl = localFileStorageService.uploadImageLocally(image);
            logger.info("Image uploaded successfully to local storage: {}", imageUrl);
        } else {
            logger.info("Using DigitalOcean Spaces for image upload");
            imageUrl = spacesService.uploadImage(image);
            logger.info("Image uploaded successfully to Spaces: {}", imageUrl);
        }

        return imageUrl;
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
        Property property = propertyRepository.findById(id).orElse(null);
        if (property != null && property.getImageUrl() != null) {
            // Delete image from storage (local or cloud)
            if (property.getImageUrl().startsWith("/uploads/")) {
                // Local storage
                localFileStorageService.deleteImageLocally(property.getImageUrl());
            } else {
                // DigitalOcean Spaces
                spacesService.deleteImage(property.getImageUrl());
            }
        }
        propertyRepository.deleteById(id);
    }
}