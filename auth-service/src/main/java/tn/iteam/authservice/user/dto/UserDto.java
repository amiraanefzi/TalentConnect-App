package tn.iteam.authservice.user.dto;

import java.time.Instant;
import java.util.Set;

public record UserDto(
        Long id,
        String email,
        Set<String> roles,
        boolean enabled,
        Instant createdAt
) {
}

