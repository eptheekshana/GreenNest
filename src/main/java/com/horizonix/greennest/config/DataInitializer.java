package com.horizonix.greennest.config;

import com.horizonix.greennest.entity.Property;
import com.horizonix.greennest.entity.Role;
import com.horizonix.greennest.entity.User;
import com.horizonix.greennest.repository.PropertyRepository;
import com.horizonix.greennest.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PropertyRepository propertyRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        try {
            // Check if sample data already exists
            if (propertyRepository.findByStatusOrderByCreatedAtDesc("APPROVED").isEmpty()) {
                initializeSampleData();
                logger.info("Sample data initialized successfully");
            } else {
                logger.info("Sample data already exists");
            }
        } catch (Exception e) {
            logger.error("Error initializing sample data: " + e.getMessage(), e);
            // Don't fail the application startup if sample data initialization fails
        }
    }

    private void initializeSampleData() {
        // Create a sample owner user if not exists
        User owner = userRepository.findUserByEmail("owner@example.com").orElse(null);

        if (owner == null) {
            owner = new User();
            owner.setEmail("owner@example.com");
            owner.setPassword(passwordEncoder.encode("password123"));
            owner.setFullName("John Property Owner");
            owner.setContactNumber("0771234567");
            owner.setRole(Role.OWNER);
            owner.setVerified(true);
            owner.setEnabled(true);
            userRepository.save(owner);
        }

        // Create sample properties
        createPropertyIfNotExists(owner,
            "Modern Boarding House Near NSBM",
            "Colombo 05",
            "A spacious and well-maintained boarding house with modern amenities. Includes WiFi, 24/7 security, and a common study area.",
            15000.0,
            "boarding-room.JPEG"
        );

        createPropertyIfNotExists(owner,
            "Cozy Student Room in Colombo",
            "Colombo 06",
            "Comfortable single room with attached bathroom. Located within walking distance to NSBM and local markets.",
            8500.0,
            "boarding-room.JPEG"
        );

        createPropertyIfNotExists(owner,
            "Luxurious Annex with Garden",
            "Colombo 04",
            "Premium annex apartment with beautiful garden view. Perfect for groups of 2-3 students. Includes kitchen and living area.",
            22000.0,
            "boarding-room.JPEG"
        );

        createPropertyIfNotExists(owner,
            "Budget-Friendly Shared Room",
            "Colombo 07",
            "Affordable shared room option suitable for students on a budget. Basic amenities provided, clean and secure environment.",
            5500.0,
            "boarding-room.JPEG"
        );

        createPropertyIfNotExists(owner,
            "Studio Apartment with WiFi",
            "Colombo 03",
            "Self-contained studio apartment with kitchenette and private bathroom. High-speed WiFi and parking available.",
            12000.0,
            "boarding-room.JPEG"
        );

        createPropertyIfNotExists(owner,
            "Family-Owned Boarding Home",
            "Colombo 08",
            "Warm and welcoming boarding home managed by a local family. Includes home-cooked meals and laundry service.",
            10000.0,
            "boarding-room.JPEG"
        );
    }

    private void createPropertyIfNotExists(User owner, String title, String location,
                                          String description, Double price, String imageName) {
        // Check if property already exists
        if (propertyRepository.findByStatusOrderByCreatedAtDesc("APPROVED")
                .stream()
                .noneMatch(p -> p.getTitle().equals(title))) {

            Property property = new Property();
            property.setTitle(title);
            property.setLocation(location);
            property.setDescription(description);
            property.setPrice(price);
            property.setImageName(imageName);
            property.setOwner(owner);
            property.setStatus("APPROVED");
            property.setCreatedAt(LocalDateTime.now());

            propertyRepository.save(property);
        }
    }
}

