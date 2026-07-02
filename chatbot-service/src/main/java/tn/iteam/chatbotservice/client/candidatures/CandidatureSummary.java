package tn.iteam.chatbotservice.client.candidatures;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.Instant;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CandidatureSummary(
    Long id,
    Long offerId,
    String status,
    Instant createdAt
) {}

