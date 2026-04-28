package com.horizonix.nboard.service;

import com.horizonix.nboard.entity.Role;
import com.horizonix.nboard.entity.User;
import com.horizonix.nboard.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserService implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired private UserRepository userRepository;
    @Autowired private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;
    @Autowired private EmailService emailService;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        logger.debug("Loading user by email: {}", email);
        User user = userRepository.findByEmail(email);
        if (user == null) {
            logger.warn("User not found with email: {}", email);
            throw new UsernameNotFoundException("Invalid email or password");
        }

        // Check if user is verified (especially for OWNER role)
        if (!user.isVerified() && user.getRole() == Role.OWNER) {
            logger.warn("OWNER user not verified: {}", email);
            throw new UsernameNotFoundException("Your account is pending admin verification. Please wait for approval.");
        }

        if (!user.isEmailVerified()) {
            logger.warn("Email is not verified for user: {}", email);
            throw new DisabledException("Please verify your email with OTP before logging in.");
        }

        // Check if user is enabled
        if (!user.isEnabled()) {
            logger.warn("User account disabled: {}", email);
            throw new UsernameNotFoundException("Your account has been disabled. Please contact support.");
        }

        logger.debug("User found: {} with enabled={} and authorities={}", email, user.isEnabled(), user.getAuthorities());
        return user;
    }

    // Ensure this method is named 'saveUser' exactly
    public User saveUser(User user) {
        try {
            // Set default role if not specified
            if (user.getRole() == null) {
                user.setRole(Role.STUDENT);
                logger.info("Role was null, set to default STUDENT");
            }

            // Hash the password
            user.setPassword(passwordEncoder.encode(user.getPassword()));

            // Clear confirmPassword to avoid validation issues during save
            user.setConfirmPassword(null);

            user.setEnabled(true); // Enable login immediately
            user.setEmailVerified(false);

            // Generate and set OTP
            String otp = generateOtp();
            user.setOtp(otp);
            user.setOtpExpiryTime(LocalDateTime.now().plusMinutes(10)); // OTP valid for 10 minutes

            if (user.getRole() == Role.OWNER) {
                user.setVerified(false); // Owners need admin approval
            } else {
                user.setVerified(true); // Students are immediately verified
            }

            logger.info("Saving user to repository: email={}, role={}, enabled={}, verified={}, emailVerified={}",
                user.getEmail(), user.getRole(), user.isEnabled(), user.isVerified(), user.isEmailVerified());

            User savedUser = userRepository.save(user);
            logger.info("User successfully saved with ID: {}", savedUser.getId());

            // Send OTP email
            try {
                emailService.sendOtpEmail(savedUser.getEmail(), otp);
                logger.info("OTP email sent successfully to: {}", savedUser.getEmail());
            } catch (Exception emailException) {
                logger.error("Failed to send OTP email to {}. User still registered but email not sent.", savedUser.getEmail(), emailException);
                // Don't throw the exception - user is already saved. They can resend OTP later.
            }

            return savedUser;
        } catch (Exception e) {
            logger.error("Exception during user save for email: {}", user.getEmail(), e);
            throw e;
        }
    }

    public boolean verifyEmail(String email, String otp) {
        User user = userRepository.findByEmail(email);
        if (user == null || otp == null || otp.isBlank()) {
            return false;
        }

        if (user.getOtp() == null || user.getOtpExpiryTime() == null) {
            return false;
        }

        if (!otp.equals(user.getOtp()) || user.getOtpExpiryTime().isBefore(LocalDateTime.now())) {
            return false;
        }

        user.setEmailVerified(true);
        user.setOtp(null);
        user.setOtpExpiryTime(null);
        userRepository.save(user);
        return true;
    }

    private String generateOtp() {
        // Generate a 6-digit OTP
        return String.format("%06d", new SecureRandom().nextInt(999999));
    }

    // Ensure helper methods exist
    public User findByEmail(String email) { return userRepository.findByEmail(email); }
    public boolean isEmailTaken(String email) { return userRepository.findByEmail(email) != null; }

    public List<User> getPendingOwners() { return userRepository.findByRoleAndIsVerifiedFalse(Role.OWNER); }
    public void approveOwner(Long id) {
        User user = userRepository.findById(id).orElse(null);
        if (user != null) { user.setVerified(true); userRepository.save(user); }
    }
}