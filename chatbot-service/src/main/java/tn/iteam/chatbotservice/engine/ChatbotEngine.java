package tn.iteam.chatbotservice.engine;

import org.springframework.stereotype.Component;

import java.text.Normalizer;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;

@Component
public class ChatbotEngine {

    private static final Pattern DIACRITICS = Pattern.compile("\\p{M}+");
    private static final Pattern TOKEN_SPLIT = Pattern.compile("[^a-z0-9]+");

    private final List<IntentRule> rules = List.of(
            new IntentRule(
                    "greeting",
                    Set.of("bonjour", "salut", "hello", "hi", "coucou", "bonsoir", "hey", "salam", "yo"),
                    List.of(
                            "Bonjour! Je suis l'assistant TalentConnect. Comment puis-je vous aider?",
                            "Salut! Je suis là pour vous aider avec les offres d'emploi, vos candidatures et vos questions RH.",
                            "Bienvenue! Que puis-je faire pour vous aujourd'hui?"
                    )
            ),
            new IntentRule(
                    "job_search",
                    Set.of("emploi", "job", "poste", "offre", "recherche", "recrutement", "travail", "embauche", "candidat", "candidaturer", "travailler", "embaucher", "recruter", "offres", "postes"),
                    List.of(
                            "Pour une recherche d'emploi, indiquez le métier, la localisation et le niveau d'expérience souhaité.",
                            "Je peux vous orienter vers les offres pertinentes. Precisez le type de poste que vous cherchez.",
                            "Quel type d'emploi vous intéresse? Dites-moi le domaine, le secteur ou le niveau d'expérience."
                    )
            ),
            new IntentRule(
                    "career",
                    Set.of("carriere", "formation", "competence", "evolution", "developpement", "progression", "carrière", "compétence", "développement", "apprentissage", "skills", "skill", "bilan", "coaching", "mentorat"),
                    List.of(
                            "Pour progresser, identifiez une compétence cible et associez-la à un objectif de poste.",
                            "Je peux vous aider à structurer un plan de formation selon votre objectif professionnel.",
                            "Pour avancer dans votre carriere, explorez les formations disponibles et les opportunites d'évolution interne."
                    )
            ),
            new IntentRule(
                    "benefits",
                    Set.of("avantage", "avantages", "salaire", "prime", "bonus", "assurance", "conges", "congés", "rmuneration", "rémunération", "paie", "indemnite", "indemnités", "couverture", "maladie", "retraite"),
                    List.of(
                            "Les avantages dépendent du poste et du contrat. Consultez l'offre ou demandez le détail au recruteur.",
                            "Pour les questions de salaire et avantages, comparez le poste, le niveau et la localisation.",
                            "Chaque offre detaille les conditions salariales et avantages sociaux. N'hesitez pas à les négocier lors de l'entretien."
                    )
            ),
            new IntentRule(
                    "company",
                    Set.of("culture", "equipe", "valeur", "valeurs", "mission", "environnement", "équipe", "vision", "éthique", "collaboratif", "autonomie", "innovation", "climat", "ambiance"),
                    List.of(
                            "La culture d'équipe se vérifie avec des exemples concrets: rituels, autonomie et façon de collaborer.",
                            "Posez des questions sur les valeurs, la mission et les modes de travail pendant l'entretien.",
                            "Découvrez la culture interne en visitant les bureaux, en rencontrant l'équipe et en consultant les avis des employés."
                    )
            ),
            new IntentRule(
                    "application",
                    Set.of("candidature", "statut", "dossier", "reponse", "entretien", "réponse", "confirmation", "sélection", "classement", "convocation", "interview"),
                    List.of(
                            "Pour suivre une candidature, ouvrez votre tableau de bord et vérifiez le statut du dossier.",
                            "Si le statut n'a pas changé, contactez le recruteur avec la référence de candidature.",
                            "Les entretiens se déroulent généralement en plusieurs étapes. Un email de confirmation vous sera envoyé avant chaque rendez-vous."
                    )
            ),
            new IntentRule(
                    "support",
                    Set.of("support", "aide", "probleme", "erreur", "contact", "bloque", "problème", "blocage", "help", "assistance", "soutien", "urgence", "signaler", "faire un signalement"),
                    List.of(
                            "Décrivez le problème, l'action effectuée et le message d'erreur. Je vous aiderai à isoler la cause.",
                            "Pour un problème technique, indiquez la page concernée, votre rôle et les étapes pour reproduire.",
                            "Contactez notre support: support@talentconnect.tn ou utilisez le formulaire de contact sur la plateforme."
                    )
            ),
            new IntentRule(
                    "goodbye",
                    Set.of("bye", "ciao", "adieu", "revoir", "bientot", "bientôt", "au revoir", "à bientôt", "à revoir", "salut", "tchao", "bisous"),
                    List.of(
                            "Au revoir! Revenez quand vous avez besoin d'aide sur TalentConnect.",
                            "À bientôt! Bonne continuation dans vos démarches.",
                            "Merci de votre visite. À très bientôt!"
                    )
            )
    );

    public BotReply replyTo(String message) {
        String normalized = normalize(message);
        Set<String> tokens = Set.of(TOKEN_SPLIT.split(normalized));

        return rules.stream()
                .map(rule -> new IntentMatch(rule, rule.matchScore(normalized, tokens)))
                .filter(match -> match.score() > 0)
                .max(java.util.Comparator.comparingInt(IntentMatch::score)
                        .thenComparingInt(IntentMatch::businessPriority))
                .map(IntentMatch::rule)
                .map(rule -> new BotReply(rule.intent(), rule.selectResponse(normalized)))
                .orElseGet(() -> new BotReply("fallback", fallback(normalized)));
    }

    private String fallback(String normalized) {
        List<String> responses = List.of(
                "Je n'ai pas assez d'information pour répondre précisément. Pouvez-vous reformuler?",
                "Précisez votre besoin: offres d'emploi, suivi de candidature, développement de carrière, avantages salariaux ou support technique.",
                "Je peux vous aider si vous ajoutez un peu plus de contexte à votre question.",
                "Essayez de me demander à propos de: recherche d'emploi, formation, salaire, culture d'entreprise ou statut de candidature.",
                "Je ne suis pas sûr d'avoir bien compris. Pouvez-vous reformuler votre question?"
        );
        return responses.get(indexFor(normalized, responses.size()));
    }

    private static String normalize(String value) {
        String lower = value == null ? "" : value.toLowerCase(Locale.ROOT).trim();
        String decomposed = Normalizer.normalize(lower, Normalizer.Form.NFD);
        return DIACRITICS.matcher(decomposed).replaceAll("");
    }

    private static int indexFor(String value, int size) {
        return Math.floorMod(value.hashCode(), size);
    }

    private record IntentRule(String intent, Set<String> keywords, List<String> responses) {

        int matchScore(String normalizedMessage, Set<String> tokens) {
            int exactMatches = (int) keywords.stream().filter(keyword -> {
                if (keyword.contains(" ")) {
                    return normalizedMessage.contains(keyword);
                }
                return tokens.contains(keyword);
            }).count();

            // Fuzzy matching for typos and misspellings
            int fuzzyMatches = (int) keywords.stream()
                    .filter(keyword -> !keyword.contains(" ")) // Only for single words
                    .filter(keyword -> {
                        // Check fuzzy match against tokens
                        return tokens.stream()
                                .anyMatch(token -> FuzzyMatcher.isSimilar(token, keyword, 75));
                    })
                    .count();

            // Exact matches count more than fuzzy matches
            // Exact: weight 3, Fuzzy: weight 1
            return (exactMatches * 3) + fuzzyMatches;
        }

        String selectResponse(String normalizedMessage) {
            return responses.get(indexFor(normalizedMessage, responses.size()));
        }
    }

    private record IntentMatch(IntentRule rule, int score) {

        int businessPriority() {
            return "greeting".equals(rule.intent()) ? 0 : 1;
        }
    }
}
