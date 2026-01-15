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

    // Show the Login Page
    @GetMapping("/login")
    public String showLoginPage() {
        return "login"; // Looks for login.html in templates folder
    }

    // Show the Registration Page
    @GetMapping("/register")
    public String showRegisterPage(Model model) {
        // We pass an empty User object to the form so Thymeleaf can bind data to it
        model.addAttribute("user", new User());
        return "register"; // Looks for register.html
    }

    // Handle the Registration Form Submission
    @PostMapping("/register")
    public String registerUser(@ModelAttribute("user") User user, Model model) {

        // Check if email already exists
        if (userService.emailExists(user.getEmail())) {
            model.addAttribute("error", "There is already an account registered with that email");
            return "register"; // Return to form with error
        }

        // Save the user
        userService.registerUser(user);

        // Redirect to login page with a success message
        return "redirect:/login?success";
    }
}