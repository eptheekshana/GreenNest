package com.horizonix.greennest.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data // Generates Getters, Setters, ToString
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Column(nullable = false, unique = true)
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    @Column(nullable = false)
    private String password;

    @NotBlank(message = "Full name is required")
    @Column(name = "full_name", nullable = false)
    private String fullName;

    // Roles: ROLE_STUDENT, ROLE_OWNER, ROLE_ADMIN
    @Column(nullable = false)
    private String role;

    // Admin Verification Logic: Owners start as false, Students start as true
    @Column(name = "is_verified", nullable = false)
    private boolean isVerified = false;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        // Auto-verify students, but require approval for owners
        if ("ROLE_STUDENT".equals(this.role)) {
            this.isVerified = true;
        } else {
            this.isVerified = false;
        }
    }
}