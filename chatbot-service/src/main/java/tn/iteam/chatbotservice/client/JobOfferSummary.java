package tn.iteam.chatbotservice.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * DTO léger représentant une offre d'emploi récupérée depuis job-service.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record JobOfferSummary(
        String id,
        String title,
        String companyName,
        String location,
        String employmentType,
        String experienceLevel,
        boolean remote,
        Integer salaryMin,
        Integer salaryMax,
        String currency,
        boolean published
) {
    /** Formate l'offre en une ligne lisible pour le chatbot. */
    public String toLine() {
        StringBuilder sb = new StringBuilder("• ")
                .append(title)
                .append(" — ").append(companyName)
                .append(" (").append(location).append(")");
        if (remote) sb.append(" 🏠 Remote");
        if (experienceLevel != null) sb.append(" | ").append(experienceLevel);
        if (employmentType != null) sb.append(" | ").append(employmentType);
        if (salaryMin != null && salaryMax != null) {
            sb.append(" | ").append(salaryMin).append("–").append(salaryMax);
            if (currency != null) sb.append(" ").append(currency);
        }
        return sb.toString();
    }
}

