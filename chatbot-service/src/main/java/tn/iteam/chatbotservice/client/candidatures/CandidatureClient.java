package tn.iteam.chatbotservice.client.candidatures;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
public class CandidatureClient {
    private static final Logger log = LoggerFactory.getLogger(CandidatureClient.class);
    private final RestTemplate restTemplate;
    private final String candidaturesServiceBaseUrl;

    public CandidatureClient(RestTemplate restTemplate,
                             @Value("${chatbot.candidatures-service.base-url:http://localhost:8084}") String candidaturesServiceBaseUrl) {
        this.restTemplate = restTemplate;
        this.candidaturesServiceBaseUrl = candidaturesServiceBaseUrl;
    }

    public List<CandidatureSummary> getUserCandidatures(Long userId) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("X-User-Id", userId.toString());
            headers.set("X-Role", "EMPLOYEE");
            HttpEntity<Void> entity = new HttpEntity<>(headers);

            CandidatureListResponse response = restTemplate.exchange(
                candidaturesServiceBaseUrl + "/api/candidatures/me",
                HttpMethod.GET,
                entity,
                CandidatureListResponse.class
            ).getBody();

            return response != null ? response.content() : List.of();
        } catch (Exception e) {
            log.warn("Impossible de récupérer les candidatures de l'utilisateur {}: {}", userId, e.getMessage());
            return List.of();
        }
    }
}

