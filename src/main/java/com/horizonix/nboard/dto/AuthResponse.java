package com.horizonix.nboard.dto;

public record AuthResponse(
        String token,
        String tokenType,
        long expiresIn,
        String email,
        String fullName,
        String role
) {
}

