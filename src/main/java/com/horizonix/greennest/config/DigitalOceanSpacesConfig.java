package com.horizonix.greennest.config;

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

    @Value("${do.spaces.key}")
    private String accessKey;

    @Value("${do.spaces.secret}")
    private String secretKey;

    @Value("${do.spaces.endpoint}")
    private String endpoint;

    @Value("${do.spaces.region}")
    private String region;

    @Bean
    public AmazonS3 digitalOceanSpacesClient() {
        // Check if credentials are configured
        if (accessKey == null || accessKey.equals("YOUR_SPACES_ACCESS_KEY") ||
            secretKey == null || secretKey.equals("YOUR_SPACES_SECRET_KEY")) {
            logger.error("DigitalOcean Spaces credentials are not configured!");
            logger.error("Please update application.properties with valid credentials.");
            logger.error("Image uploads will fail until credentials are set.");
        }

        BasicAWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);

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

