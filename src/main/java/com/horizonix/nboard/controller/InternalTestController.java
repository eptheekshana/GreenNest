package com.horizonix.nboard.controller;

import jakarta.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class InternalTestController {

    private static final Logger logger = LoggerFactory.getLogger(InternalTestController.class);

    private final JavaMailSender mailSender;
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public InternalTestController(JavaMailSender mailSender, DataSource dataSource) {
        this.mailSender = mailSender;
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @GetMapping("/internal/test-mail")
    public Map<String, Object> testMail(@RequestParam(name = "to", required = false) String to) {
        try {
            String recipient = (to == null || to.isBlank()) ? "test@example.com" : to;
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setTo(recipient);
            msg.setFrom("no-reply@nboard.com");
            msg.setSubject("Nboard test mail");
            msg.setText("This is a test email from Nboard internal test endpoint.");
            mailSender.send(msg);
            logger.info("Test email sent to {}", recipient);
            return Map.of("status", "ok", "message", "mail sent to " + recipient);
        } catch (Exception ex) {
            logger.error("Failed to send test mail", ex);
            return Map.of("status", "error", "message", ex.getMessage());
        }
    }

    @GetMapping("/internal/test-db")
    public Map<String, Object> testDb() {
        try {
            Integer result = jdbcTemplate.queryForObject("SELECT 1", Integer.class);
            logger.info("DB test query returned: {}", result);
            return Map.of("status", "ok", "result", result);
        } catch (Exception ex) {
            logger.error("DB connectivity test failed", ex);
            return Map.of("status", "error", "message", ex.getMessage());
        }
    }
}

