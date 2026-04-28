package com.horizonix.nboard.service;

import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

@Service
public class SendGridEmailService implements EmailService {

    private static final Logger logger = LoggerFactory.getLogger(SendGridEmailService.class);

    private final JavaMailSender mailSender;

    @Value("${mail.from.email:no-reply@nboard.com}")
    private String fromEmail;

    @Value("${mail.from.name:Nboard}")
    private String fromName;

    public SendGridEmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void sendOtpEmail(String to, String otp) {
        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, false, StandardCharsets.UTF_8.name());
            helper.setFrom(fromEmail, fromName);
            helper.setTo(to);
            helper.setSubject("Your OTP for Nboard Registration");
            helper.setText(
                    "<html><body>" +
                            "<h2>Welcome to Nboard!</h2>" +
                            "<p>Your OTP for email verification is:</p>" +
                            "<h1 style='color: #007bff; font-size: 32px; letter-spacing: 2px;'>" + otp + "</h1>" +
                            "<p>This OTP is valid for 10 minutes.</p>" +
                            "<p>If you didn't request this, please ignore this email.</p>" +
                            "</body></html>",
                    true
            );
            mailSender.send(message);
            logger.info("Successfully sent OTP email to {}", to);
        } catch (Exception ex) {
            logger.error("Error sending OTP email to {}", to, ex);
            throw new RuntimeException("Failed to send OTP email. Please try again later.", ex);
        }
    }
}

