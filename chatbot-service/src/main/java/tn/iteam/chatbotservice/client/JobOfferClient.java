package tn.iteam.chatbotservice.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

/**
 * Client HTTP vers job-service pour récupérer les offres d'emploi réelles.
 * Appelle GET /api/jobs (endpoint public, pas d'auth requise).
 */
@Component
public class JobOfferClient {

    private static final Logger log = LoggerFactory.getLogger(JobOfferClient.class);

    private final RestTemplate restTemplate;
    private final String jobServiceBaseUrl;

    public JobOfferClient(
            RestTemplate restTemplate,
            @Value("${chatbot.job-service.base-url:http://localhost:8085}") String jobServiceBaseUrl) {
        this.restTemplate = restTemplate;
        this.jobServiceBaseUrl = jobServiceBaseUrl;
    }

    /** Toutes les offres publiées (max 20). */
    public List<JobOfferSummary> findPublished() {
        return fetch(UriComponentsBuilder
                .fromHttpUrl(jobServiceBaseUrl + "/api/jobs")
                .queryParam("size", 20)
                .toUriString());
    }

    /** Offres par localisation. */
    public List<JobOfferSummary> findByLocation(String location) {
        return fetch(UriComponentsBuilder
                .fromHttpUrl(jobServiceBaseUrl + "/api/jobs")
                .queryParam("location", location)
                .queryParam("size", 10)
                .toUriString());
    }

    /** Offres remote uniquement. */
    public List<JobOfferSummary> findRemote() {
        return fetch(UriComponentsBuilder
                .fromHttpUrl(jobServiceBaseUrl + "/api/jobs")
                .queryParam("remote", true)
                .queryParam("size", 10)
                .toUriString());
    }

    /** Offres par type de contrat (FULL_TIME, PART_TIME, CONTRACT, INTERN, TEMPORARY). */
    public List<JobOfferSummary> findByEmploymentType(String employmentType) {
        return fetch(UriComponentsBuilder
                .fromHttpUrl(jobServiceBaseUrl + "/api/jobs")
                .queryParam("employmentType", employmentType)
                .queryParam("size", 10)
                .toUriString());
    }

    /** Offres par mot-clé (titre, description, entreprise). */
    public List<JobOfferSummary> findByKeyword(String keyword) {
        return fetch(UriComponentsBuilder
                .fromHttpUrl(jobServiceBaseUrl + "/api/jobs")
                .queryParam("q", keyword)
                .queryParam("size", 10)
                .toUriString());
    }

    // ─── privé ─────────────────────────────────────────────────────────────

    private List<JobOfferSummary> fetch(String url) {
        try {
            JobOfferPage page = restTemplate.getForObject(url, JobOfferPage.class);
            return page != null && page.content() != null ? page.content() : List.of();
        } catch (Exception ex) {
            log.warn("Impossible de contacter job-service ({}): {}", url, ex.getMessage());
            return List.of();
        }
    }
}

