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

    // Method to register a new user
    public void registerUser(User user) {
        // Encrypt the plain text password (e.g., "123456" -> "$2a$10$...")
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);

        // Save to database
        userRepository.save(user);
    }

    // Check if email already exists
    public boolean emailExists(String email) {
        return userRepository.findByEmail(email).isPresent();
    }
}