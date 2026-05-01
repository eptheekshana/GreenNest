package com.horizonix.nboard.service;

import com.horizonix.nboard.entity.Role;
import com.horizonix.nboard.entity.User;
import com.horizonix.nboard.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
public class UserService implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired private UserRepository userRepository;
    @Autowired private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;
    @Autowired private EmailVerificationService emailVerificationService;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        logger.debug("Loading user by email: {}", email);
        User user = userRepository.findByEmail(email);
        if (user == null) {
            logger.warn("User not found with email: {}", email);
            throw new UsernameNotFoundException("Invalid email or password");
        }

        if (!user.isEmailVerified()) {
            logger.warn("User email not verified: {}", email);
            throw new UsernameNotFoundException("Please verify your email before logging in.");
        }

        // Check if user is verified (especially for OWNER role)
        if (!user.isVerified() && user.getRole() == Role.OWNER) {
            logger.warn("OWNER user not verified: {}", email);
            throw new UsernameNotFoundException("Your account is pending admin verification. Please wait for approval.");
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
    public String saveUser(User user) {
        return saveUser(user, null);
    }

    public String saveUser(User user, String verificationBaseUrl) {
        // Set default role if not specified
        user.setRole(Objects.requireNonNullElse(user.getRole(), Role.STUDENT));

        // Hash the password
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Clear confirmPassword to avoid validation issues during save
        user.setConfirmPassword(null);

        String verificationToken = UUID.randomUUID().toString();
        user.setVerificationToken(verificationToken);
        user.setVerificationTokenExpiresAt(LocalDateTime.now().plusHours(24));

        user.setEnabled(true);
        user.setEmailVerified(false);

        if (user.getRole() == Role.OWNER) {
            user.setVerified(false); // Owners need admin approval
        } else {
            user.setVerified(true); // Students are immediately verified
        }

        User savedUser = userRepository.save(user);
        try {
            // sendVerificationEmail now returns the verification URL (for fallback/testing)
            String verificationUrl = emailVerificationService.sendVerificationEmail(savedUser, verificationToken, verificationBaseUrl);
            return verificationUrl;
        } catch (RuntimeException ex) {
            userRepository.delete(savedUser);
            throw ex;
        }
    }

    public boolean verifyEmail(String token) {
        if (token == null || token.isBlank()) {
            return false;
        }

        User user = userRepository.findByVerificationToken(token.trim());
        if (user == null) {
            return false;
        }

        if (user.getVerificationTokenExpiresAt() != null && user.getVerificationTokenExpiresAt().isBefore(LocalDateTime.now())) {
            logger.warn("Verification token expired for {}", user.getEmail());
            return false;
        }

        user.setEmailVerified(true);
        user.setEnabled(true);
        user.setVerificationToken(null);
        user.setVerificationTokenExpiresAt(null);
        userRepository.save(user);
        return true;
    }

    // Ensure helper methods exist
    public User findByEmail(String email) { return userRepository.findByEmail(email); }
    public boolean isEmailTaken(String email) { return userRepository.findByEmail(email) != null; }

    public List<User> getPendingOwners() { return userRepository.findByRoleAndIsVerifiedFalse(Role.OWNER); }
    public void approveOwner(Long id) {
        User user = userRepository.findById(id).orElse(null);
        if (user != null) {
            user.setVerified(true);
            user.setEnabled(true);
            userRepository.save(user);
        }
    }
}