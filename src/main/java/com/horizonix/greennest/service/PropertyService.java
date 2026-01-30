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

    public List<Property> getAllProperties() {
        return propertyRepository.findAll();
    }

    public Property getPropertyById(Long id) {
        return propertyRepository.findById(id).orElseThrow(() -> new RuntimeException("Property not found"));
    }

    public List<Property> getPropertiesByOwner(User owner) {
        return propertyRepository.findByOwner(owner);
    }

    public void saveProperty(Property property, MultipartFile image) throws IOException {
        if (image != null && !image.isEmpty()) {
            String fileName = UUID.randomUUID().toString() + "_" + image.getOriginalFilename();
            Path path = Paths.get(uploadDir + fileName);
            Files.createDirectories(path.getParent());
            Files.copy(image.getInputStream(), path);
            property.setImageName(fileName);
        }
        propertyRepository.save(property);
    }

    public Page<Property> searchProperties(String location, Double price, Pageable pageable) {
        if (location == null) location = "";
        if (price == null) price = 1000000.0;
        return propertyRepository.findByLocationContainingIgnoreCaseAndPriceLessThanEqual(location, price, pageable);
    }

    public void deleteProperty(Long id) {
        propertyRepository.deleteById(id);
    }
}