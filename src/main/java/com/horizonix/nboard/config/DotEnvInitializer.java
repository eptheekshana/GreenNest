package com.horizonix.nboard.config;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * Loads environment variables from .env file for local development
 * This allows IntelliJ and other IDEs to automatically load the .env file
 */
public class DotEnvInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        Path envFile = Paths.get(".env");

        // Only load if .env exists (local development)
        if (!Files.exists(envFile)) {
            System.out.println("No .env file found. Using system environment variables.");
            return;
        }

        try {
            Map<String, Object> envProperties = loadEnvFile(envFile);
            if (!envProperties.isEmpty()) {
                MutablePropertySources propertySources = applicationContext.getEnvironment().getPropertySources();
                propertySources.addFirst(new MapPropertySource("dotenv", envProperties));
                System.out.println("✓ Loaded " + envProperties.size() + " environment variables from .env file");
            }
        } catch (IOException e) {
            System.err.println("Warning: Could not load .env file: " + e.getMessage());
        }
    }

    private Map<String, Object> loadEnvFile(Path envFile) throws IOException {
        Map<String, Object> properties = new HashMap<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(envFile.toFile()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();

                // Skip empty lines and comments
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }

                // Parse KEY=VALUE
                int equalsIndex = line.indexOf('=');
                if (equalsIndex > 0) {
                    String key = line.substring(0, equalsIndex).trim();
                    String value = line.substring(equalsIndex + 1).trim();

                    // Remove quotes if present
                    if ((value.startsWith("\"") && value.endsWith("\"")) ||
                        (value.startsWith("'") && value.endsWith("'"))) {
                        value = value.substring(1, value.length() - 1);
                    }

                    properties.put(key, value);
                }
            }
        }

        return properties;
    }
}

