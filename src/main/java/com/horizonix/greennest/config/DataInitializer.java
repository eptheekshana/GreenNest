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
            // Run initialization logic
            initializeSampleData();
        } catch (Exception e) {
            logger.error("Error initializing data: " + e.getMessage(), e);
        }
    }

    private void initializeSampleData() {

        // --- 1. CREATE ADMIN USER (This is what you need) ---
        if (userRepository.findByEmail("admin@greennest.com") == null) {
            User admin = new User();
            admin.setFullName("Super Admin");
            admin.setEmail("admin@greennest.com");
            admin.setPassword(passwordEncoder.encode("admin123")); // 🔒 Password: admin123
            admin.setRole(Role.ADMIN); // Ensure Role.ADMIN exists in your enum/string
            admin.setVerified(true);
            admin.setEnabled(true);

            userRepository.save(admin);
            logger.info("Admin account created: admin@greennest.com / admin123");
        } else {
            logger.info("Admin account already exists.");
        }

        // --- 2. EXISTING SAMPLE DATA LOGIC (Kept your existing code) ---
        if (propertyRepository.findByStatusOrderByCreatedAtDesc("APPROVED").isEmpty()) {

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
                    "A spacious and well-maintained boarding house...",
                    15000.0,
                    "boarding-room.JPEG"
            );

            // ... (rest of your existing properties) ...

            logger.info("Sample properties initialized successfully");
        }
    }

    private void createPropertyIfNotExists(User owner, String title, String location,
                                           String description, Double price, String imageName) {
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