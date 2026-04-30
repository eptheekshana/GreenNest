package com.horizonix.nboard.repository;

import com.horizonix.nboard.entity.Role;
import com.horizonix.nboard.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    // Required for Login
    User findByEmail(String email);

    // Optional version for safer null handling
    Optional<User> findUserByEmail(String email);

    User findByVerificationToken(String verificationToken);

    // Required for Admin Dashboard (Jan 19 Task)
    // "Find all users who have THIS role and are NOT verified"
    List<User> findByRoleAndIsVerifiedFalse(Role role);
}