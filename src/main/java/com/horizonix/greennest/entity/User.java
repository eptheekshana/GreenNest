package com.horizonix.greennest.entity;

import jakarta.persistence.*;
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

    @Column(nullable = false, unique = true) private String email;
    @Column(nullable = false) private String password;
    @Column(nullable = false) private String fullName;

    // Add this field (Matches your HTML form)
    @Column(name = "contact_number")
    private String contactNumber;

    // ✅ FIX 2: Add logic fields
    private boolean enabled = true;
    private boolean isVerified = false;

    @Enumerated(EnumType.STRING) private Role role;

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Property> properties = new ArrayList<>();

    public User() {}

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