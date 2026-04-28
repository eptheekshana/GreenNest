package com.horizonix.nboard.repository;

import com.horizonix.nboard.entity.EmailVerificationOtp;
import com.horizonix.nboard.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface EmailVerificationOtpRepository extends JpaRepository<EmailVerificationOtp, Long> {

    Optional<EmailVerificationOtp> findTopByUserAndConsumedAtIsNullOrderByCreatedAtDesc(User user);

    boolean existsByUserAndConsumedAtIsNullAndCreatedAtAfter(User user, LocalDateTime createdAfter);
}

