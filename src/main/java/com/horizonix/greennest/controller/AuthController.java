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

    @GetMapping("/login")
    public String showLoginPage() { return "login"; }

    @GetMapping("/register")
    public String showRegisterPage(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("user") User user,
                               BindingResult result,
                               Model model) {
        if (result.hasErrors()) { return "register"; }

        if (userService.isEmailTaken(user.getEmail())) {
            result.rejectValue("email", null, "Email is already registered.");
            return "register";
        }

        // Calls 'saveUser' (Matches Service)
        userService.saveUser(user);

        return "redirect:/login?success";
    }
}