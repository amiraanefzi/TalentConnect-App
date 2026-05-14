package tn.iteam.authservice.kafka;

import java.time.Instant;
import java.util.Set;

public record UserCreatedEvent(
        Long id,
        String email,
        Set<String> roles,
        Instant createdAt
) {
}

