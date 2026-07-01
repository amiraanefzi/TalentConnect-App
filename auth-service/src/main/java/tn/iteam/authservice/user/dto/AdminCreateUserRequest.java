package tn.iteam.authservice.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.Set;

/**
 * Requête POST /api/admin/users et POST /api/users
 * Crée un utilisateur avec compte + profil en un seul appel.
 * Champs obligatoires : email, password.
 * Champs optionnels  : roles, firstName, lastName, department, location, title, ...
 */
public record AdminCreateUserRequest(
        // ── Compte (obligatoires) ──────────────────────────────────
        @Email @NotBlank String email,
        @NotBlank @Size(min = 8, max = 72) String password,
        Set<String> roles,

        // ── Profil de base (optionnels) ────────────────────────────
        String firstName,
        String lastName,
        String department,
        String location,
        String title,
        Integer experienceYears,

        // ── Profil étendu (optionnels) ─────────────────────────────
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
) {
}
