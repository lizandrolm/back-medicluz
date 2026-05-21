package com.medicluz.auth.dto;

import com.medicluz.user.entity.Role;
import lombok.Builder;

@Builder
public record AuthResponse(
    String accessToken,
    String refreshToken,
    String tokenType,
    long expiresIn,
    Long userId,
    String fullName,
    String email,
    Role role
) {
    public static AuthResponseBuilder defaults() {
        return AuthResponse.builder().tokenType("Bearer");
    }
}
