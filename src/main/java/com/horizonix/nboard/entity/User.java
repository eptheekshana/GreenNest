package com.horizonix.nboard.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "users")
public class User implements UserDetails {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    @Column(nullable = false, unique = true) private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    @Column(nullable = false) private String password;

    @NotBlank(message = "Full name is required")
    @Column(nullable = false) private String fullName;

    // Transient field for password confirmation
    @Transient
    private String confirmPassword;

    @NotBlank(message = "Contact number is required")
    @Pattern(regexp = "[0-9]{10}", message = "Contact number must be 10 digits")
    @Column(name = "contact_number")
    private String contactNumber;

    // Add logic fields
    private boolean enabled = true;
    private boolean isVerified = false;

    // Keeps compatibility with existing DB schema where email_verified is NOT NULL.
    @Column(name = "email_verified", nullable = false)
    private boolean emailVerified = true;

    @NotNull(message = "Role is required")
    @Enumerated(EnumType.STRING) private Role role;

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Property> properties = new ArrayList<>();

    public User() {}

    // Validation: Ensure passwords match
    @AssertTrue(message = "Passwords do not match")
    public boolean isPasswordMatching() {
        // Skip validation if confirmPassword is null (e.g., during login or password updates)
        if (confirmPassword == null || confirmPassword.isEmpty()) {
            return true;
        }
        return password != null && password.equals(confirmPassword);
    }

    // --- UserDetails Logic ---
    @Override public Collection<? extends GrantedAuthority> getAuthorities() {
        if (role == null) return Collections.emptyList();
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }
    @Override public String getPassword() { return password; }
    @Override public String getUsername() { return email; }
    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return enabled; }
}