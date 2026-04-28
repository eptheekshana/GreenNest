package com.horizonix.nboard.service;

import com.horizonix.nboard.entity.User;
import com.horizonix.nboard.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class EmailVerificationService {

    private static final Logger logger = LoggerFactory.getLogger(EmailVerificationService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    /**
     * Send OTP email for user registration
     */
    public void sendRegistrationOtp(User user) {
        if (user.getOtp() == null || user.getOtp().isEmpty()) {
            logger.error("User {} has no OTP generated", user.getEmail());
            throw new IllegalStateException("OTP not generated for user");
        }

        try {
            emailService.sendOtpEmail(user.getEmail(), user.getOtp());
            logger.info("Registration OTP sent to: {}", user.getEmail());
        } catch (Exception e) {
            logger.error("Failed to send registration OTP to {}", user.getEmail(), e);
            throw new RuntimeException("Failed to send OTP email", e);
        }
    }

    /**
     * Verify OTP and mark email as verified
     */
    public boolean verifyOtp(String email, String otp) {
        User user = userRepository.findByEmail(email);

        if (user == null || otp == null || otp.isBlank()) {
            logger.warn("User not found: {}", email);
            return false;
        }

        if (user.getOtp() == null || user.getOtpExpiryTime() == null) {
            logger.warn("No active OTP found for user: {}", email);
            return false;
        }

        if (!otp.equals(user.getOtp())) {
            logger.warn("Invalid OTP for user: {}", email);
            return false;
        }

        if (user.getOtpExpiryTime() == null || user.getOtpExpiryTime().isBefore(LocalDateTime.now())) {
            logger.warn("OTP expired for user: {}", email);
            return false;
        }

        // Mark email as verified
        user.setEmailVerified(true);
        user.setOtp(null);
        user.setOtpExpiryTime(null);
        userRepository.save(user);

        logger.info("Email verified for user: {}", email);
        return true;
    }

    /**
     * Resend OTP to user
     */
    public void resendOtp(String email) {
        User user = userRepository.findByEmail(email);

        if (user == null) {
            logger.warn("User not found: {}", email);
            throw new IllegalArgumentException("User not found");
        }

        if (user.isEmailVerified()) {
            logger.warn("Email already verified for user: {}", email);
            throw new IllegalStateException("Email already verified");
        }

        // Generate new OTP
        String newOtp = generateOtp();
        user.setOtp(newOtp);
        user.setOtpExpiryTime(LocalDateTime.now().plusMinutes(10));
        userRepository.save(user);

        try {
            emailService.sendOtpEmail(email, newOtp);
            logger.info("Resent OTP to: {}", email);
        } catch (Exception e) {
            logger.error("Failed to resend OTP to {}", email, e);
            throw new RuntimeException("Failed to send OTP email", e);
        }
    }

    /**
     * Generate 6-digit OTP
     */
    private String generateOtp() {
        return String.format("%06d", new java.security.SecureRandom().nextInt(999999));
    }
}

