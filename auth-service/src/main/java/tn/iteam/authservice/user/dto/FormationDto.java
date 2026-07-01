package tn.iteam.authservice.user.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Formation académique d'un utilisateur.
 * Stockée en JSON dans la colonne formations_json.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record FormationDto(
        String institution,
        String degree,
        String fieldOfStudy,
        Integer startYear,
        Integer endYear,
        String description
) {}

