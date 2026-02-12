package com.horizonix.greennest.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
public class DigitalOceanSpacesService {

    private static final Logger logger = LoggerFactory.getLogger(DigitalOceanSpacesService.class);

    @Autowired
    private AmazonS3 spacesClient;

    @Value("${do.spaces.bucket}")
    private String bucketName;

    @Value("${do.spaces.endpoint}")
    private String endpoint;

    @Value("${do.spaces.key}")
    private String accessKey;

    /**
     * Upload an image to DigitalOcean Spaces and return the public URL
     */
    public String uploadImage(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IOException("File is empty or null");
        }

        // Check if credentials are configured
        if (accessKey == null || accessKey.equals("YOUR_SPACES_ACCESS_KEY")) {
            logger.error("DigitalOcean Spaces credentials are not configured!");
            throw new IOException("Image upload is not configured. Please contact the administrator to set up DigitalOcean Spaces credentials.");
        }

        // Generate unique filename
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename != null && originalFilename.contains(".")
                ? originalFilename.substring(originalFilename.lastIndexOf("."))
                : "";
        String fileName = "properties/" + UUID.randomUUID().toString() + extension;

        try {
            // Set metadata
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(file.getSize());
            metadata.setContentType(file.getContentType());

            // Upload to Spaces
            PutObjectRequest putObjectRequest = new PutObjectRequest(
                    bucketName,
                    fileName,
                    file.getInputStream(),
                    metadata
            ).withCannedAcl(CannedAccessControlList.PublicRead);

            spacesClient.putObject(putObjectRequest);

            // Construct and return public URL
            String publicUrl = String.format("https://%s.%s/%s", bucketName, endpoint, fileName);

            logger.info("Image uploaded successfully to Spaces: {}", publicUrl);
            return publicUrl;

        } catch (Exception e) {
            logger.error("Failed to upload image to Spaces: {}", e.getMessage());

            // Provide helpful error message
            if (e.getMessage() != null && e.getMessage().contains("credentials")) {
                throw new IOException("Invalid DigitalOcean Spaces credentials. Please check your configuration.", e);
            } else if (e.getMessage() != null && e.getMessage().contains("bucket")) {
                throw new IOException("DigitalOcean Spaces bucket not found. Please verify the bucket name.", e);
            } else {
                throw new IOException("Failed to upload image: " + e.getMessage(), e);
            }
        }
    }

    /**
     * Delete an image from DigitalOcean Spaces
     */
    public void deleteImage(String imageUrl) {
        try {
            // Extract key from URL (everything after bucket.endpoint/)
            String key = imageUrl.substring(imageUrl.indexOf(endpoint) + endpoint.length() + 1);
            spacesClient.deleteObject(bucketName, key);
            logger.info("Image deleted successfully from Spaces: {}", key);
        } catch (Exception e) {
            logger.error("Failed to delete image from Spaces: {}", e.getMessage());
        }
    }
}

