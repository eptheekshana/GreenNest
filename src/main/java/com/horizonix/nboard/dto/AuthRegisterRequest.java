package com.horizonix.nboard.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record AuthRegisterRequest(
        @NotBlank(message = "Email is required")
        @Email(message = "Email must be valid")
        String email,

        @NotBlank(message = "Password is required")
        @Size(min = 6, message = "Password must be at least 6 characters")
        String password,

        @NotBlank(message = "Confirm password is required")
        String confirmPassword,

        @NotBlank(message = "Full name is required")
        String fullName,

        @NotBlank(message = "Contact number is required")
        @Pattern(regexp = "[0-9]{10}", message = "Contact number must be 10 digits")
        String contactNumber,

        String role
) {
}

