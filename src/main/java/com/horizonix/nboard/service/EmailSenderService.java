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
public class EmailSenderService {

    private static final Logger logger = LoggerFactory.getLogger(EmailSenderService.class);

    private final JavaMailSender mailSender;

    @Value("${mail.from.email:no-reply@nboard.com}")
    private String fromEmail;

    @Value("${mail.from.name:Nboard}")
    private String fromName;

    public EmailSenderService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendOtpEmail(String toEmail, String recipientName, String otpCode, long expiresInMinutes) {
        String subject = "Verify your Nboard email";
        String safeName = (recipientName == null || recipientName.isBlank()) ? "there" : recipientName;
        String text = "<html><body>"
                + "<p>Hello " + safeName + ",</p>"
                + "<p>Your Nboard verification code is:</p>"
                + "<h1 style='color:#007bff; font-size:32px; letter-spacing:2px;'>" + otpCode + "</h1>"
                + "<p>This code expires in " + expiresInMinutes + " minutes.</p>"
                + "<p>If you did not create this account, you can ignore this email.</p>"
                + "</body></html>";

        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, false, StandardCharsets.UTF_8.name());
            helper.setFrom(fromEmail, fromName);
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(text, true);
            mailSender.send(message);
        } catch (Exception ex) {
            logger.error("Failed to send verification email to {}", toEmail, ex);
            throw new IllegalStateException("Failed to send verification email", ex);
        }
    }
}

