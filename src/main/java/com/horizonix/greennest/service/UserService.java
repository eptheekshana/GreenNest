package com.horizonix.greennest.service;

import com.horizonix.greennest.entity.Role;
import com.horizonix.greennest.entity.User;
import com.horizonix.greennest.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService; // ✅ Import this
import org.springframework.security.core.userdetails.UsernameNotFoundException; // ✅ Import this
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService implements UserDetailsService { // ✅ Implement the interface

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // This tells Spring how to find a user for login
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
    }

    // ... The rest of your existing methods ...

    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    public boolean emailExists(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    public void registerUser(User user) {
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        userRepository.save(user);
    }

    public void verifyUser(Long userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setVerified(true);
            userRepository.save(user);
        }
    }

    public boolean canUploadProperty(User user) {
        if (user == null) return false;
        if (!user.getRole().equals(Role.OWNER)) return false;
        if (!user.isVerified()) return false;
        return true;
    }
}