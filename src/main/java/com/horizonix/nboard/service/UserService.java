package com.horizonix.nboard.service;

import com.horizonix.nboard.entity.Role;
import com.horizonix.nboard.entity.User;
import com.horizonix.nboard.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class UserService implements UserDetailsService {

    @Autowired private UserRepository userRepository;
    @Autowired private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email);
        if (user == null) throw new UsernameNotFoundException("Invalid user");
        return user;
    }

    // Ensure this method is named 'saveUser' exactly
    public void saveUser(User user) {
        // Set default role if not specified
        if (user.getRole() == null) {
            user.setRole(Role.STUDENT);
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setEnabled(true); // Enable login immediately

        if (user.getRole() == Role.OWNER) {
            user.setVerified(false); // Owners need admin approval
        } else {
            user.setVerified(true); // Students are immediately verified
        }
        userRepository.save(user);
    }

    // Ensure helper methods exist
    public User findByEmail(String email) { return userRepository.findByEmail(email); }
    public boolean isEmailTaken(String email) { return userRepository.findByEmail(email) != null; }

    public List<User> getPendingOwners() { return userRepository.findByRoleAndIsVerifiedFalse(Role.OWNER); }
    public void approveOwner(Long id) {
        User user = userRepository.findById(id).orElse(null);
        if (user != null) { user.setVerified(true); userRepository.save(user); }
    }
}