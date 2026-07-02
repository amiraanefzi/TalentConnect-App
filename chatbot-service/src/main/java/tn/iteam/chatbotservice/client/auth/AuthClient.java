package tn.iteam.chatbotservice.client.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class AuthClient {
    private static final Logger log = LoggerFactory.getLogger(AuthClient.class);
    private final RestTemplate restTemplate;
    private final String authServiceBaseUrl;

    public AuthClient(RestTemplate restTemplate,
                      @Value("${chatbot.auth-service.base-url:http://localhost:8081}") String authServiceBaseUrl) {
        this.restTemplate = restTemplate;
        this.authServiceBaseUrl = authServiceBaseUrl;
    }

    public UserSummary getUserProfile(Long userId) {
        try {
            // Note: Normalement il faudrait un token technique ou passer le token utilisateur,
            // mais ici on suppose que l'accès interne est autorisé ou via endpoint dédié.
            // On utilise l'endpoint existant GET /api/users/{id} (RH/ADMIN dans API_CONTRACT)
            return restTemplate.getForObject(authServiceBaseUrl + "/api/users/" + userId, UserSummary.class);
        } catch (Exception e) {
            log.warn("Impossible de récupérer le profil utilisateur {}: {}", userId, e.getMessage());
            return null;
        }
    }
}

