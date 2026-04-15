package com.horizonix.nboard.dto;

public record UserProfileResponse(
        Long id,
        String email,
        String fullName,
        String contactNumber,
        String role,
        boolean enabled,
        boolean verified
) {
}

