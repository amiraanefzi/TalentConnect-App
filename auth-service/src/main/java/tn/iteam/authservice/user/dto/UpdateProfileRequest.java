package tn.iteam.authservice.user.dto;

import java.util.List;

/**
 * Requête PUT /api/users/profile
 * Tous les champs sont optionnels — seuls les non-null sont mis à jour.
 */
public record UpdateProfileRequest(
        String firstName,
        String lastName,
        String department,
        String location,
        String title,
        Integer experienceYears,
        String avatarUrl,
        String phone,
        String address,
        String bio,
        String linkedinUrl,
        String githubUrl,
        List<String> languages,
        List<String> skills,
        List<FormationDto> formations,
        List<ExperienceDto> experiences
) {}

