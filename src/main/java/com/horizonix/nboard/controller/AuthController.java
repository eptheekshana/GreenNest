package com.horizonix.nboard.controller;

import com.horizonix.nboard.entity.User;
import com.horizonix.nboard.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
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

        // Check if email is already taken
        if (userService.isEmailTaken(user.getEmail())) {
            logger.warn("Email already registered: {}", user.getEmail());
            result.rejectValue("email", "error.email", "Email is already registered.");
            return "register";
        }

        try {
            String verificationUrl = userService.saveUser(user, ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString());
            logger.info("User registered successfully: {} with role: {}", user.getEmail(), user.getRole());
            // If verificationUrl is returned (SendGrid not configured) we still redirect to login
            return "redirect:/login?verificationSent";
        } catch (IllegalStateException e) {
            logger.error("Email verification configuration failed for {}", user.getEmail(), e);
            result.reject("registration.email", e.getMessage());
            return "register";
        } catch (Exception e) {
            logger.error("Error saving user: {}", user.getEmail(), e);
            result.reject("registration.error", "An error occurred during registration. Please try again.");
            return "register";
        }
    }

    @GetMapping("/verify-email")
    public String verifyEmail(@RequestParam("token") String token) {
        boolean verified = userService.verifyEmail(token);
        if (!verified) {
            return "redirect:/login?verificationError";
        }

        return "redirect:/login?verified";
    }
}