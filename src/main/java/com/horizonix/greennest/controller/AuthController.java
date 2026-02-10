package com.horizonix.greennest.controller;

import com.horizonix.greennest.entity.User;
import com.horizonix.greennest.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    // --- Standard User Login ---
    @GetMapping("/login")
    public String showLoginPage() { return "login"; }

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
        if (result.hasErrors()) { return "register"; }

        if (userService.isEmailTaken(user.getEmail())) {
            result.rejectValue("email", "error.email", "Email is already registered.");
            return "register";
        }

        userService.saveUser(user);

        // Redirect with role-specific message
        if (user.getRole().toString().equals("OWNER")) {
            return "redirect:/login?success=ownerWait";
        } else {
            return "redirect:/login?success";
        }
    }
}