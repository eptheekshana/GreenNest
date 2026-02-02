package com.horizonix.greennest.controller;

import com.horizonix.greennest.entity.User;
import com.horizonix.greennest.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import jakarta.validation.Valid; // uses jakarta for Spring Boot 3+ (v4.0.1 in your logs)

@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    // =========================================================
    // 1. LOGIN PAGE
    // =========================================================
    @GetMapping("/login")
    public String showLoginPage() {
        return "login"; // Loads login.html
    }

    // =========================================================
    // 2. REGISTRATION PAGE
    // =========================================================
    @GetMapping("/register")
    public String showRegisterPage(Model model) {
        // We pass a new User object to the form so Thymeleaf can bind data to it
        model.addAttribute("user", new User());
        return "register"; // Loads register.html
    }

    // =========================================================
    // 3. HANDLE REGISTRATION (POST)
    // =========================================================
    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("user") User user,
                               BindingResult result,
                               Model model) {

        // A. Validation Check: Did the user leave fields empty?
        if (result.hasErrors()) {
            return "register"; // Return to form with error messages displayed
        }

        // B. Duplicate Email Check
        User existing = userService.findByEmail(user.getEmail());
        if (existing != null) {
            // Add a manual error to the "email" field
            result.rejectValue("email", null, "There is already an account registered with that email");
            return "register";
        }

        // C. Save the User
        // (The Service handles password encryption and verification status)
        userService.save(user);

        // D. Redirect based on Role (UX Polish)
        // If they are an OWNER, we show the "Wait for Verification" alert [cite: 8]
        if (user.getRole().name().equals("OWNER")) {
            return "redirect:/login?success=ownerWait";
        }

        // If STUDENT, standard success message
        return "redirect:/login?success";
    }
}