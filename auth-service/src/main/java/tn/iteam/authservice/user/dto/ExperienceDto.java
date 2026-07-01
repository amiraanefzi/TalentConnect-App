package tn.iteam.authservice.user.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Expérience professionnelle d'un utilisateur.
 * Stockée en JSON dans la colonne experiences_json.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ExperienceDto(
        String company,
        String title,
        String location,
        String startDate,   // format: "2022-01"
        String endDate,     // format: "2024-06" ou null si current=true
        boolean current,
        String description
) {}

