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

    // --- 1. LOGIN PAGE ---
    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }

    // --- 2. REGISTRATION PAGE ---
    @GetMapping("/register")
    public String showRegisterPage(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    // --- 3. HANDLE REGISTRATION ---
    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("user") User user,
                               BindingResult result,
                               Model model) {

        // A. Validation Check
        if (result.hasErrors()) {
            return "register";
        }

        // B. Duplicate Email Check
        // (Ensure userService has this method. If not, use userRepository directly or add the helper)
        User existing = userService.findByEmail(user.getEmail());
        if (existing != null) {
            result.rejectValue("email", null, "There is already an account registered with that email");
            return "register";
        }

        // C. Save the User
        // Changed 'save' to 'saveUser' to match your Service
        userService.saveUser(user);

        // D. Redirect Logic
        if (user.getRole().name().equals("OWNER")) {
            return "redirect:/login?success=ownerWait";
        }
        return "redirect:/login?success";
    }
}