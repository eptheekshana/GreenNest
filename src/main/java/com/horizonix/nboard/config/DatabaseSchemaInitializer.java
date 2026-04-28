package com.horizonix.nboard.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;

@Component
public class DatabaseSchemaInitializer implements ApplicationRunner {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseSchemaInitializer.class);

    private final JdbcTemplate jdbcTemplate;
    private final DataSource dataSource;

    public DatabaseSchemaInitializer(JdbcTemplate jdbcTemplate, DataSource dataSource) {
        this.jdbcTemplate = jdbcTemplate;
        this.dataSource = dataSource;
    }

    @Override
    public void run(ApplicationArguments args) {
        try (Connection connection = dataSource.getConnection()) {
            String product = connection.getMetaData().getDatabaseProductName();
            if (product == null || !product.toLowerCase().contains("mysql")) {
                return;
            }

            ensureColumnExists("users", "otp", "varchar(6) NULL");
            ensureColumnExists("users", "otp_expiry_time", "datetime(6) NULL");
            ensureColumnExists("users", "email_verified", "bit(1) NOT NULL DEFAULT b'0'");
            ensureColumnExists("users", "is_verified", "bit(1) NOT NULL DEFAULT b'0'");
            ensureColumnExists("users", "enabled", "bit(1) NOT NULL DEFAULT b'1'");
        } catch (Exception ex) {
            logger.warn("Database schema initializer skipped due to error: {}", ex.getMessage());
        }
    }

    private void ensureColumnExists(String tableName, String columnName, String columnDefinition) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM information_schema.columns " +
                        "WHERE table_schema = DATABASE() AND table_name = ? AND column_name = ?",
                Integer.class,
                tableName,
                columnName
        );

        if (count != null && count > 0) {
            return;
        }

        String sql = String.format("ALTER TABLE %s ADD COLUMN %s %s", tableName, columnName, columnDefinition);
        jdbcTemplate.execute(sql);
        logger.info("Added missing column {}.{}", tableName, columnName);
    }
}

