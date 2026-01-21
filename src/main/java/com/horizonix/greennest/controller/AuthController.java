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

@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    // 1. Show the Registration Form
    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        // We pass an empty User object so Thymeleaf can bind the form fields to it
        model.addAttribute("user", new User());
        return "register"; // This looks for register.html
    }

    // 2. Handle the Form Submission
    @PostMapping("/register")
    public String registerUser(@ModelAttribute("user") User user,
                               BindingResult result,
                               Model model) {

        // Check if email already exists
        if (userService.emailExists(user.getEmail())) {
            result.rejectValue("email", "error.user", "There is already an account registered with this email");
        }

        // If there are errors (like duplicate email), reload the form
        if (result.hasErrors()) {
            return "register";
        }

        // Save the user (The logic we wrote earlier handles password hashing)
        userService.registerUser(user);

        // Redirect to login with a success message
        return "redirect:/login?success";
    }

    // 3. Show Login Page (You'll need a login.html later)
    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }
}