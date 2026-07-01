package tn.iteam.authservice.user.dto;

import java.time.Instant;
import java.util.List;
import java.util.Set;

public record UserDto(
        Long id,
        String employeeId,
        String email,
        Set<String> roles,
        boolean enabled,
        Instant createdAt,
        // ── Infos de base ──────────────────────────────────────────────────
        String firstName,
        String lastName,
        String department,
        String location,
        String title,
        int experienceYears,
        String avatarUrl,
        // ── Profil étendu ──────────────────────────────────────────────────
        String phone,
        String address,
        String bio,
        String linkedinUrl,
        String githubUrl,
        List<String> languages,
        List<String> skills,
        List<FormationDto> formations,
        List<ExperienceDto> experiences
) {
}



