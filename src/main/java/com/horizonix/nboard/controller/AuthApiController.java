package com.horizonix.nboard.controller;

import com.horizonix.nboard.dto.AuthLoginRequest;
import com.horizonix.nboard.dto.AuthRegisterRequest;
import com.horizonix.nboard.dto.AuthResponse;
import com.horizonix.nboard.dto.UserProfileResponse;
import com.horizonix.nboard.entity.Role;
import com.horizonix.nboard.entity.User;
import com.horizonix.nboard.security.JwtService;
import com.horizonix.nboard.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthApiController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtService jwtService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody AuthLoginRequest request,
                                   HttpServletRequest httpRequest) {
        User existingUser = userService.findByEmail(request.email());
        if (existingUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Invalid email or password"));
        }

        if (!existingUser.isEmailVerified()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "Please verify your email before logging in."));
        }

        if (!existingUser.isVerified() && existingUser.getRole() == Role.OWNER) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "Your account is pending admin verification. Please wait for approval."));
        }

        if (!existingUser.isEnabled()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "Your account has been disabled. Please contact support."));
        }

        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.email(), request.password())
            );
        } catch (BadCredentialsException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Invalid email or password"));
        }

        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(authentication);
        SecurityContextHolder.setContext(securityContext);
        httpRequest.getSession(true).setAttribute(
                HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                securityContext
        );

        User authenticatedUser = existingUser;
        String token = jwtService.generateToken(authenticatedUser);

        return ResponseEntity.ok(new AuthResponse(
                token,
                "Bearer",
                jwtService.getExpirationMs() / 1000,
                authenticatedUser.getEmail(),
                authenticatedUser.getFullName(),
                authenticatedUser.getRole().name()
        ));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody AuthRegisterRequest request) {
        if (userService.isEmailTaken(request.email())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message", "Email is already registered."));
        }

        if (!request.password().equals(request.confirmPassword())) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Passwords do not match."));
        }

        User user = new User();
        user.setEmail(request.email());
        user.setPassword(request.password());
        user.setConfirmPassword(request.confirmPassword());
        user.setFullName(request.fullName());
        user.setContactNumber(request.contactNumber());

        Role role = Role.STUDENT;
        if (request.role() != null && !request.role().isBlank()) {
            try {
                role = Role.valueOf(request.role().trim().toUpperCase());
            } catch (IllegalArgumentException ignored) {
                return ResponseEntity.badRequest().body(Map.of("message", "Invalid role value."));
            }
        }
        user.setRole(role);

        userService.saveUser(user, ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("message", "Registration successful. Please check your email to verify your account."));
    }

    @GetMapping("/verify-email")
    public ResponseEntity<?> verifyEmail(@RequestParam("token") String token) {
        boolean verified = userService.verifyEmail(token);
        if (!verified) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "Invalid or expired verification token."));
        }

        return ResponseEntity.ok(Map.of("message", "Email verified successfully."));
    }

    @GetMapping("/me")
    public ResponseEntity<?> me(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof UserDetails userDetails)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Unauthorized"));
        }

        User user = userService.findByEmail(userDetails.getUsername());
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "User not found"));
        }

        return ResponseEntity.ok(new UserProfileResponse(
                user.getId(),
                user.getEmail(),
                user.getFullName(),
                user.getContactNumber(),
                user.getRole().name(),
                user.isEnabled(),
                user.isVerified()
        ));
    }
}

