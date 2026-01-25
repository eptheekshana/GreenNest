package com.horizonix.greennest.controller;

import com.horizonix.greennest.entity.User;
import com.horizonix.greennest.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    // 1. Show Login Page
    @GetMapping("/login")
    public String showLoginPage() {
        return "login"; // Looks for login.html in templates folder
    }

    // 2. Show Registration Page
    @GetMapping("/register")
    public String showRegisterPage(Model model) {
        model.addAttribute("user", new User()); // Empty user object for the form
        return "register"; // Looks for register.html
    }

    // 3. Handle Registration Form Submission
    @PostMapping("/register")
    public String registerUser(@ModelAttribute("user") User user) {

        // Check if email already exists
        User existing = userService.findByEmail(user.getEmail());
        if (existing != null) {
            return "redirect:/register?error=emailExists";
        }

        // Save the user (Password encoding happens inside Service)
        userService.save(user);

        // Redirect based on Role (UX Polish)
        // If they are an OWNER, warn them they need approval.
        if (user.getRole().name().equals("OWNER")) {
            return "redirect:/login?success=ownerWait";
        }

        return "redirect:/login?success";
    }
}