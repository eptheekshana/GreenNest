package com.horizonix.nboard.controller;

import com.horizonix.nboard.entity.User;
import com.horizonix.nboard.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private UserService userService;

    // --- Standard User Login ---
    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }

    // ---  HIDDEN ADMIN LOGIN ---
    // Access this by typing: http://localhost:8080/secret-admin-entry
    @GetMapping("/secret-admin-entry")
    public String showHiddenAdminLogin() {
        return "admin/admin-login"; // Looks for templates/admin/admin-login.html
    }

    // --- Registration Logic ---
    @GetMapping("/register")
    public String showRegisterPage(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("user") User user,
                               BindingResult result) {
        logger.info("Registration attempt for email: {}", user.getEmail());

        // Check for validation errors
        if (result.hasErrors()) {
            logger.warn("Validation errors during registration:");
            result.getAllErrors().forEach(error ->
                logger.warn("  - {}: {}", error.getObjectName(), error.getDefaultMessage())
            );
            return "register";
        }

        // Check if email is already taken (handle DB errors gracefully)
        try {
            if (userService.isEmailTaken(user.getEmail())) {
                logger.warn("Email already registered: {}", user.getEmail());
                result.rejectValue("email", "error.email", "Email is already registered.");
                return "register";
            }
        } catch (DataAccessException dae) {
            logger.error("Database error while checking existing email for {}", user.getEmail(), dae);
            result.reject("registration.error", "Database connection issue. Please try again shortly.");
            return "register";
        }

        try {
            logger.info("Attempting to save user: {} with role: {}", user.getEmail(), user.getRole());
            User savedUser = userService.saveUser(user);
            logger.info("User registered successfully: {} with role: {}", user.getEmail(), user.getRole());
            return "redirect:/verify-email?email=" + savedUser.getEmail();
        } catch (DataIntegrityViolationException dive) {
            logger.error("Data integrity violation during registration for {}", user.getEmail(), dive);
            result.reject("registration.error", "Registration failed due to duplicate or invalid data. Please check email/contact number.");
            return "register";
        } catch (DataAccessException dae) {
            logger.error("Database access error during registration for {}", user.getEmail(), dae);
            String cause = dae.getMostSpecificCause() != null ? dae.getMostSpecificCause().getMessage() : dae.getMessage();
            if (cause != null && (cause.contains("Unknown column") || cause.contains("otp_expiry_time") || cause.contains(" otp "))) {
                result.reject("registration.error", "Registration setup is incomplete. Please try again in 1 minute.");
            } else {
                result.reject("registration.error", "Database connection issue. Please try again in a moment.");
            }
            return "register";
        } catch (IllegalStateException ise) {
            logger.error("Configuration error during registration for {}: {}", user.getEmail(), ise.getMessage());
            result.reject("registration.error", "Email service is not configured. Please contact administrator.");
            return "register";
        } catch (Exception e) {
            logger.error("Error saving user: {}", user.getEmail(), e);
            String errorMessage = "An error occurred during registration. ";

            // Provide more specific error messages
            if (e.getMessage() != null && e.getMessage().contains("Duplicate entry")) {
                errorMessage += "This email is already registered.";
            } else if (e.getMessage() != null && e.getMessage().contains("constraint")) {
                errorMessage += "Invalid input data.";
            } else {
                errorMessage += "Please try again or contact support.";
            }

            result.reject("registration.error", errorMessage);
            return "register";
        }
    }
}