package com.horizonix.greennest.service;

import com.horizonix.greennest.repository.PropertyRepository;
import com.horizonix.greennest.entity.Property;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@Service
public class PropertyService {

    @Autowired
    private PropertyRepository propertyRepository;

    // Define where images will be saved
    // This saves to: YourProject/src/main/resources/static/uploads/
    private final String UPLOAD_DIR = System.getProperty("user.dir") + "/src/main/resources/static/uploads/";

    public List<Property> getAllProperties() {
        return propertyRepository.findAll();
    }

    public void saveProperty(Property property, MultipartFile imageFile) throws IOException {

        // 1. Handle the Image Upload
        if (imageFile != null && !imageFile.isEmpty()) {

            // Create the directory if it doesn't exist
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Generate a unique filename (to prevent overwriting)
            String fileName = UUID.randomUUID().toString() + "_" + imageFile.getOriginalFilename();

            // Save the file to the folder
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(imageFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // Save ONLY the filename to the database
            property.setImageName(fileName);
        }

        // 2. Save the Property Data to Database
        propertyRepository.save(property);
    }

    public Property getPropertyById(Long id) {
        return propertyRepository.findById(id).orElse(null);
    }
}