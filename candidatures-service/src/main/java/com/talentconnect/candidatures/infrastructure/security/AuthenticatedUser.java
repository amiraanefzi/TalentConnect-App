package com.talentconnect.candidatures.infrastructure.security;

public record AuthenticatedUser(Long userId, String role) {
}
