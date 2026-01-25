package com.horizonix.greennest.service;

import com.horizonix.greennest.entity.Role;
import com.horizonix.greennest.entity.User;
import com.horizonix.greennest.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // --- Authentication Logic ---
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("Invalid username or password.");
        }
        return user;
    }

    // --- Registration Logic ---
    public void save(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        // Admins and Students are auto-verified. Owners must wait for approval.
        if (user.getRole() == Role.OWNER) {
            user.setVerified(false);
        } else {
            user.setVerified(true);
        }
        userRepository.save(user);
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    // --- Admin Verification Logic (Jan 19 Task) ---

    // 1. Find all owners who are NOT yet verified
    public List<User> getPendingOwners() {
        return userRepository.findByRoleAndIsVerifiedFalse(Role.OWNER);
    }

    // 2. Approve an owner
    public void approveOwner(Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user != null) {
            user.setVerified(true);
            userRepository.save(user);
        }
    }

    // --- Security Check Logic (Jan 17 Task) ---
    public boolean canUploadProperty(User user) {
        return user.getRole() == Role.OWNER && user.isVerified();
    }
}