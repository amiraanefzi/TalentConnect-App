package tn.iteam.authservice.user.dto;

import java.util.Set;

public record AuthResponse(
        String accessToken,
        String tokenType,
        Long userId,
        String email,
        Set<String> roles
) {
}

