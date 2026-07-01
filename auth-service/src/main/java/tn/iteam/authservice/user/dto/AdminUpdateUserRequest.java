package tn.iteam.authservice.user.dto;

import java.util.List;

/**
 * Requête PUT /api/admin/users/{id}
 * Permet à un ADMIN de modifier le profil complet d'un utilisateur.
 * Tous les champs sont optionnels.
 */
public record AdminUpdateUserRequest(
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

