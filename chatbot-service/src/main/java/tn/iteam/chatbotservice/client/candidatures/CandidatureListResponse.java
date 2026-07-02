package tn.iteam.chatbotservice.client.candidatures;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CandidatureListResponse(
    List<CandidatureSummary> content
) {}

