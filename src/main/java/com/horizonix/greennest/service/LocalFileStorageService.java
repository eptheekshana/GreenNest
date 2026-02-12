package com.horizonix.greennest.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

/**
 * Local file storage service as a fallback when DigitalOcean Spaces is not configured.
 * Stores images in the static/uploads directory.
 */
@Service
public class LocalFileStorageService {

    private static final Logger logger = LoggerFactory.getLogger(LocalFileStorageService.class);

    @Value("${file.upload-dir:src/main/resources/static/uploads}")
    private String uploadDir;

    /**
     * Upload an image to local storage and return the relative URL
     */
    public String uploadImageLocally(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IOException("File is empty or null");
        }

        try {
            // Create upload directory if it doesn't exist
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
                logger.info("Created upload directory: {}", uploadPath.toAbsolutePath());
            }

            // Generate unique filename
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename != null && originalFilename.contains(".")
                    ? originalFilename.substring(originalFilename.lastIndexOf("."))
                    : "";
            String fileName = UUID.randomUUID() + extension;

            // Save file
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // Return relative URL (accessible via /uploads/filename.jpg)
            String imageUrl = "/uploads/" + fileName;
            logger.info("✅ Image uploaded locally: {}", imageUrl);

            return imageUrl;

        } catch (IOException e) {
            logger.error("❌ Failed to upload image locally: {}", e.getMessage());
            throw new IOException("Failed to upload image to local storage", e);
        }
    }

    /**
     * Delete an image from local storage
     */
    public void deleteImageLocally(String imageUrl) {
        try {
            if (imageUrl != null && imageUrl.startsWith("/uploads/")) {
                String fileName = imageUrl.substring("/uploads/".length());
                Path filePath = Paths.get(uploadDir).resolve(fileName);

                if (Files.exists(filePath)) {
                    Files.delete(filePath);
                    logger.info("✅ Image deleted locally: {}", fileName);
                } else {
                    logger.warn("⚠️  Image file not found: {}", fileName);
                }
            }
        } catch (IOException e) {
            logger.error("❌ Failed to delete image locally: {}", e.getMessage());
        }
    }
}

