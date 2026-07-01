package tn.iteam.chatbotservice.engine;

import org.springframework.stereotype.Component;
import tn.iteam.chatbotservice.client.JobOfferClient;
import tn.iteam.chatbotservice.client.JobOfferSummary;

import java.text.Normalizer;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Détecte les questions liées aux offres d'emploi et retourne
 * des réponses basées sur les VRAIES données de la base.
 *
 * Doit être consulté AVANT le ChatbotEngine (priorité sur les réponses statiques).
 */
@Component
public class JobSearchHandler {

    private static final Pattern DIACRITICS = Pattern.compile("\\p{M}+");
    private final JobOfferClient jobOfferClient;

    public JobSearchHandler(JobOfferClient jobOfferClient) {
        this.jobOfferClient = jobOfferClient;
    }

    /**
     * Tente de répondre à la question en interrogeant job-service.
     * Retourne null si la question n'est pas liée aux offres.
     */
    public String handle(String userMessage) {
        String normalized = normalize(userMessage);

        // ── Remote ──────────────────────────────────────────────────────────
        if (normalized.contains("remote") || normalized.contains("teletravail") || normalized.contains("a distance")) {
            List<JobOfferSummary> offers = jobOfferClient.findRemote();
            return buildResponse(offers, "offres en télétravail (remote)");
        }

        // ── Stage / Intern ───────────────────────────────────────────────────
        if (normalized.contains("stage") || normalized.contains("intern") || normalized.contains("stagiaire")) {
            List<JobOfferSummary> offers = jobOfferClient.findByEmploymentType("INTERN");
            return buildResponse(offers, "stages disponibles");
        }

        // ── Niveau d'expérience ──────────────────────────────────────────────
        if (normalized.contains("junior")) {
            return buildKeywordResponse("junior", "offres Junior");
        }
        if (normalized.contains("senior")) {
            return buildKeywordResponse("senior", "offres Senior");
        }
        if (normalized.contains(" mid ") || normalized.contains("intermediaire") || normalized.contains("intermédiaire")) {
            return buildKeywordResponse("mid", "offres niveau intermédiaire (Mid)");
        }
        if (normalized.contains("lead") || normalized.contains("tech lead")) {
            return buildKeywordResponse("lead", "postes Tech Lead");
        }

        // ── Localisation : villes connues ────────────────────────────────────
        String location = detectLocation(normalized);
        if (location != null) {
            List<JobOfferSummary> offers = jobOfferClient.findByLocation(location);
            return buildResponse(offers, "offres disponibles à " + capitalize(location));
        }

        // ── Contrat CDI / CDD / freelance ────────────────────────────────────
        if (normalized.contains("cdi") || normalized.contains("temps plein") || normalized.contains("full time") || normalized.contains("fulltime")) {
            List<JobOfferSummary> offers = jobOfferClient.findByEmploymentType("FULL_TIME");
            return buildResponse(offers, "offres CDI / temps plein");
        }
        if (normalized.contains("cdd") || normalized.contains("temporaire")) {
            List<JobOfferSummary> offers = jobOfferClient.findByEmploymentType("TEMPORARY");
            return buildResponse(offers, "offres CDD / temporaires");
        }
        if (normalized.contains("freelance") || normalized.contains("contrat")) {
            List<JobOfferSummary> offers = jobOfferClient.findByEmploymentType("CONTRACT");
            return buildResponse(offers, "offres en contrat / freelance");
        }
        if (normalized.contains("temps partiel") || normalized.contains("part time")) {
            List<JobOfferSummary> offers = jobOfferClient.findByEmploymentType("PART_TIME");
            return buildResponse(offers, "offres temps partiel");
        }

        // ── Liste générale des offres ────────────────────────────────────────
        if (normalized.contains("offre") || normalized.contains("poste") || normalized.contains("emploi")
                || normalized.contains("job") || normalized.contains("disponible") || normalized.contains("publie")
                || normalized.contains("recrut")) {
            List<JobOfferSummary> offers = jobOfferClient.findPublished();
            return buildResponse(offers, "offres disponibles actuellement");
        }

        // ── Recherche par mot-clé technologique ──────────────────────────────
        String techKeyword = detectTechKeyword(normalized);
        if (techKeyword != null) {
            List<JobOfferSummary> offers = jobOfferClient.findByKeyword(techKeyword);
            return buildResponse(offers, "offres liées à " + techKeyword);
        }

        return null; // Pas une question sur les offres → déléguer au ChatbotEngine
    }

    // ─── Helpers ─────────────────────────────────────────────────────────────

    private String buildKeywordResponse(String keyword, String label) {
        List<JobOfferSummary> offers = jobOfferClient.findByKeyword(keyword);
        return buildResponse(offers, label);
    }

    private String buildResponse(List<JobOfferSummary> offers, String label) {
        if (offers.isEmpty()) {
            return "Je n'ai trouvé aucune offre correspondant à votre recherche (" + label + "). "
                    + "Essayez d'élargir vos critères ou revenez plus tard.";
        }
        StringBuilder sb = new StringBuilder("Voici les ").append(label).append(" (").append(offers.size()).append(") :\n\n");
        for (JobOfferSummary offer : offers) {
            sb.append(offer.toLine()).append("\n");
        }
        sb.append("\nPour postuler, accédez à la section « Offres » de l'application.");
        return sb.toString();
    }

    private String detectLocation(String normalized) {
        for (String city : List.of("tunis", "sfax", "sousse", "monastir", "bizerte",
                "nabeul", "ariana", "la marsa", "carthage", "manouba",
                "paris", "lyon", "marseille", "remote")) {
            if (normalized.contains(city)) {
                return city;
            }
        }
        return null;
    }

    private String detectTechKeyword(String normalized) {
        for (String tech : List.of("java", "spring", "angular", "react", "python", "node",
                "javascript", "typescript", "docker", "kubernetes", "devops",
                "data", "machine learning", "ia", "sql", "mysql", "php",
                "android", "ios", "flutter", "mobile", "frontend", "backend",
                "fullstack", "full stack", "architect", "agile", "scrum")) {
            if (normalized.contains(tech)) {
                return tech;
            }
        }
        return null;
    }

    private static String normalize(String value) {
        String lower = value == null ? "" : value.toLowerCase(Locale.ROOT).trim();
        String decomposed = Normalizer.normalize(lower, Normalizer.Form.NFD);
        return DIACRITICS.matcher(decomposed).replaceAll("");
    }

    private static String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }
}

