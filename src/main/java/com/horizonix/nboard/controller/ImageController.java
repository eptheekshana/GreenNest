package com.horizonix.nboard.controller;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
public class ImageController {

    private static final Logger logger = LoggerFactory.getLogger(ImageController.class);

    @GetMapping("/uploads/{filename}")
    public ResponseEntity<Resource> getImage(@PathVariable String filename) {
        try {
            // Try multiple possible locations
            String[] possiblePaths = {
                "src/main/resources/static/uploads/" + filename,
                "target/classes/static/uploads/" + filename,
                Paths.get(System.getProperty("java.io.tmpdir"), "nboard", "uploads", filename).toString()
            };

            for (String pathStr : possiblePaths) {
                Path path = Paths.get(pathStr).normalize();
                Resource resource = new UrlResource(path.toUri());

                if (resource.exists() && resource.isReadable()) {
                    logger.info("Serving image from: {}", path.toAbsolutePath());
                    return ResponseEntity.ok()
                            .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
                            .header(HttpHeaders.CACHE_CONTROL, "public, max-age=3600")
                            .body(resource);
                }
            }

            logger.warn("⚠️ Image not found: {}", filename);
            return ResponseEntity.notFound().build();

        } catch (Exception e) {
            logger.error("❌ Error serving image: {}", filename, e);
            return ResponseEntity.notFound().build();
        }
    }
}

