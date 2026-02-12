package com.horizonix.greennest.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.IOException;
import java.nio.file.Path;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private static final Logger logger = LoggerFactory.getLogger(WebConfig.class);
    private static final String UPLOADS_DIR = "uploads";

    public WebConfig() {
        // Ensure uploads directory exists when app starts
        try {
            String[] possiblePaths = {
                "src/main/resources/static/uploads/",
                "target/classes/static/uploads/",
                Paths.get(System.getProperty("java.io.tmpdir"), "greennest", "uploads").toString()
            };

            for (String pathStr : possiblePaths) {
                Path uploadPath = Paths.get(pathStr);
                if (!Files.exists(uploadPath)) {
                    try {
                        Files.createDirectories(uploadPath);
                        logger.info("Created uploads directory: {}", uploadPath.toAbsolutePath());
                    } catch (IOException e) {
                        logger.warn("Could not create directory: {}", pathStr);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Error initializing upload directories", e);
        }
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Serve uploaded files from the file system
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(
                    "file:src/main/resources/static/uploads/",
                    "file:target/classes/static/uploads/",
                    "file:" + Paths.get(System.getProperty("java.io.tmpdir"), "greennest", "uploads").toString() + "/"
                )
                .setCachePeriod(3600);

        logger.info("Resource handlers configured for /uploads/** endpoints");
    }
}

