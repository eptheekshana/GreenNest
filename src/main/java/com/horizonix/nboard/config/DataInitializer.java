package com.horizonix.nboard.config;

import com.horizonix.nboard.entity.Property;
import com.horizonix.nboard.entity.Role;
import com.horizonix.nboard.entity.User;
import com.horizonix.nboard.repository.PropertyRepository;
import com.horizonix.nboard.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.Collections;

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
    public void run(String... args) {
        try {
            // Run initialization logic
            initializeSampleData();
        } catch (Exception e) {
            logger.error("Error initializing data: " + e.getMessage(), e);
        }
    }

    private void initializeSampleData() {

        // --- 1. CREATE ADMIN USER (This is what you need) ---
        if (userRepository.findByEmail("admin@nboard.com") == null) {
            User admin = new User();
            admin.setFullName("Super Admin");
            admin.setEmail("admin@nboard.com");
            admin.setPassword(passwordEncoder.encode("admin123")); // 🔒 Password: admin123
            admin.setContactNumber("0771111111");
            admin.setRole(Role.ADMIN); // Ensure Role.ADMIN exists in your enum/string
            admin.setVerified(true);
            admin.setEmailVerified(true);
            admin.setEnabled(true);

            userRepository.save(admin);
            logger.info("Admin account created: admin@nboard.com / admin123");
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
                owner.setEmailVerified(true);
                owner.setEnabled(true);
                userRepository.save(owner);
            }

            // Create sample properties
            createPropertyIfNotExists(owner);

            logger.info("Sample properties initialized successfully");
        }
    }

    private void createPropertyIfNotExists(User owner) {
        String title = "Modern Boarding House Near NSBM";
        if (propertyRepository.findByStatusOrderByCreatedAtDesc("APPROVED")
                .stream()
                .noneMatch(p -> p.getTitle().equals(title))) {

            Property property = new Property();
            property.setTitle(title);
            property.setLocation("Colombo 05");
            property.setDescription("A spacious and well-maintained boarding house...");
            property.setPrice(15000.0);
            // For sample data, use local path - in production, this would be Spaces URL
            property.setImageUrl("/images/boarding-room.JPEG");
            property.setPhotoUrls(Collections.singletonList("/images/boarding-room.JPEG"));
            property.setOwner(owner);
            property.setStatus("APPROVED");
            property.setCreatedAt(LocalDateTime.now());

            propertyRepository.save(property);
        }
    }
}
