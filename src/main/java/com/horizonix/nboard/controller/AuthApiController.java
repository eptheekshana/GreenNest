package com.horizonix.nboard.controller;

import com.horizonix.nboard.dto.AuthLoginRequest;
import com.horizonix.nboard.dto.AuthRegisterRequest;
import com.horizonix.nboard.dto.AuthResponse;
import com.horizonix.nboard.dto.UserProfileResponse;
import com.horizonix.nboard.entity.Role;
import com.horizonix.nboard.entity.User;
import com.horizonix.nboard.security.JwtService;
import com.horizonix.nboard.service.EmailVerificationService;
import com.horizonix.nboard.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @Autowired
    private EmailVerificationService emailVerificationService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody AuthLoginRequest request,
                                   HttpServletRequest httpRequest) {
        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.email(), request.password())
            );
        } catch (BadCredentialsException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Invalid email or password"));
        } catch (AuthenticationException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", ex.getMessage()));
        }

        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(authentication);
        SecurityContextHolder.setContext(securityContext);
        httpRequest.getSession(true).setAttribute(
                HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                securityContext
        );

        User user = userService.findByEmail(request.email());
        String token = jwtService.generateToken(user);

        return ResponseEntity.ok(new AuthResponse(
                token,
                "Bearer",
                jwtService.getExpirationMs() / 1000,
                user.getEmail(),
                user.getFullName(),
                user.getRole().name()
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

        try {
            User savedUser = userService.saveUser(user);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of(
                            "message", "Registration successful. OTP sent to email.",
                            "email", savedUser.getEmail()
                    ));
        } catch (DataIntegrityViolationException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message", "Registration failed due to duplicate or invalid data."));
        } catch (DataAccessException ex) {
            String cause = ex.getMostSpecificCause().getMessage();
            if (cause != null && (cause.contains("Unknown column") || cause.contains("otp_expiry_time") || cause.contains(" otp "))) {
                return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                        .body(Map.of("message", "Registration setup is incomplete. Please try again in 1 minute."));
            }
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(Map.of("message", "Database unavailable. Please try again shortly."));
        } catch (IllegalStateException ex) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(Map.of("message", "Email service is not configured. Please contact administrator."));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Registration failed. Please try again."));
        }
    }

    @PostMapping("/verify-email")
    public ResponseEntity<?> verifyEmail(@RequestBody Map<String, String> payload) {
        String email = payload.get("email");
        String otp = payload.get("otp");

        if (email == null || email.isBlank() || otp == null || otp.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Email and OTP are required."));
        }

        try {
            boolean verified = emailVerificationService.verifyOtp(email, otp);
            if (verified) {
                return ResponseEntity.ok(Map.of("message", "Email verified successfully."));
            }
            return ResponseEntity.badRequest().body(Map.of("message", "Invalid or expired OTP."));
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(Map.of("message", ex.getMessage()));
        }
    }

    @PostMapping("/resend-otp")
    public ResponseEntity<?> resendOtp(@RequestBody Map<String, String> payload) {
        String email = payload.get("email");

        if (email == null || email.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Email is required."));
        }

        try {
            emailVerificationService.resendOtp(email);
            return ResponseEntity.ok(Map.of("message", "OTP sent successfully."));
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(Map.of("message", ex.getMessage()));
        }
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

