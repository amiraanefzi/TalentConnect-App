package tn.iteam.chatbotservice.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/**
 * Réponse paginée de job-service (Spring Page).
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record JobOfferPage(
        List<JobOfferSummary> content,
        long totalElements
) {}

