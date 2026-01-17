package com.horizonix.greennest.service;

import com.horizonix.greennest.entity.User;
import com.horizonix.greennest.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;


    // Register a new user

    public void registerUser(User user) {
        // Encrypt the plain text password
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);

        // Save to database
        userRepository.save(user);
    }


    // Find user by email (For verification checks)

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }


    // Check if email exists (Used during registration
    public boolean emailExists(String email) {
        return userRepository.findByEmail(email).isPresent();
    }
}