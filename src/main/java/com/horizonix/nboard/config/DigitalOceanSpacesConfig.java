package com.horizonix.nboard.config;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DigitalOceanSpacesConfig {

    private static final Logger logger = LoggerFactory.getLogger(DigitalOceanSpacesConfig.class);

    @Value("${do.spaces.key:}")
    private String accessKey;

    @Value("${do.spaces.secret:}")
    private String secretKey;

    @Value("${do.spaces.endpoint:sgp1.digitaloceanspaces.com}")
    private String endpoint;

    @Value("${do.spaces.region:sgp1}")
    private String region;

    @Bean
    public AmazonS3 digitalOceanSpacesClient() {
        // Check if credentials are configured
        if (accessKey == null || accessKey.isEmpty() || accessKey.equals("YOUR_SPACES_ACCESS_KEY") ||
            secretKey == null || secretKey.isEmpty() || secretKey.equals("YOUR_SPACES_SECRET_KEY")) {
            logger.warn("DigitalOcean Spaces credentials are not configured!");
            logger.warn("Using placeholder credentials - image uploads will fail until credentials are set.");
            logger.info("To configure, set DO_SPACES_KEY and DO_SPACES_SECRET environment variables.");
        }

        // Use placeholder credentials if not configured to prevent startup failure
        String key = (accessKey != null && !accessKey.isEmpty()) ? accessKey : "placeholder_key";
        String secret = (secretKey != null && !secretKey.isEmpty()) ? secretKey : "placeholder_secret";

        BasicAWSCredentials credentials = new BasicAWSCredentials(key, secret);

        return AmazonS3ClientBuilder
                .standard()
                .withEndpointConfiguration(
                        new AwsClientBuilder.EndpointConfiguration(
                                "https://" + endpoint,
                                region
                        )
                )
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .build();
    }
}

