package tn.iteam.chatbotservice.engine;

import org.springframework.stereotype.Component;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
public class ChatbotEngine {

    private static final Pattern DIACRITICS = Pattern.compile("\\p{M}+");
    private static final Pattern TOKEN_SPLIT = Pattern.compile("[^a-z0-9]+");

    /**
     * Intent priority used only to break score ties. Higher wins.
     * When a short, ambiguous message matches two intents equally well it is
     * resolved towards the more specific / more likely intent.
     */
    private static final int PRIORITY_GREETING = 7;
    private static final int PRIORITY_APPLICATION = 6;
    private static final int PRIORITY_BENEFITS = 5;
    private static final int PRIORITY_JOB_SEARCH = 4;
    private static final int PRIORITY_CAREER = 3;
    private static final int PRIORITY_COMPANY = 2;
    private static final int PRIORITY_SUPPORT = 1;
    private static final int PRIORITY_GOODBYE = 0;

    private final List<IntentRule> rules = List.of(
            new IntentRule(
                    "greeting",
                    PRIORITY_GREETING,
                    Set.of("bonjour", "salut", "hello", "hi", "coucou", "bonsoir", "hey", "salam", "yo"),
                    List.of(
                            "Bonjour! Je suis l'assistant TalentConnect. Comment puis-je vous aider?",
                            "Salut! Je suis là pour vous aider avec les offres d'emploi, vos candidatures et vos questions RH.",
                            "Bienvenue! Que puis-je faire pour vous aujourd'hui?"
                    )
            ),
            new IntentRule(
                    "job_search",
                    PRIORITY_JOB_SEARCH,
                    Set.of("emploi", "job", "poste", "offre", "recherche", "recrutement", "travail", "embauche", "candidat", "candidaturer", "travailler", "embaucher", "recruter", "offres", "postes"),
                    List.of(
                            "Pour une recherche d'emploi, indiquez le métier, la localisation et le niveau d'expérience souhaité.",
                            "Je peux vous orienter vers les offres pertinentes. Precisez le type de poste que vous cherchez.",
                            "Quel type d'emploi vous intéresse? Dites-moi le domaine, le secteur ou le niveau d'expérience."
                    )
            ),
            new IntentRule(
                    "career",
                    PRIORITY_CAREER,
                    Set.of("carriere", "formation", "competence", "evolution", "developpement", "progression", "carrière", "compétence", "développement", "apprentissage", "skills", "skill", "bilan", "coaching", "mentorat"),
                    List.of(
                            "Pour progresser, identifiez une compétence cible et associez-la à un objectif de poste.",
                            "Je peux vous aider à structurer un plan de formation selon votre objectif professionnel.",
                            "Pour avancer dans votre carriere, explorez les formations disponibles et les opportunites d'évolution interne."
                    )
            ),
            new IntentRule(
                    "benefits",
                    PRIORITY_BENEFITS,
                    Set.of("avantage", "avantages", "salaire", "prime", "bonus", "assurance", "conges", "congés", "rémunération", "remuneration", "paie", "indemnite", "indemnités", "couverture", "maladie", "retraite"),
                    List.of(
                            "Les avantages dépendent du poste et du contrat. Consultez l'offre ou demandez le détail au recruteur.",
                            "Pour les questions de salaire et avantages, comparez le poste, le niveau et la localisation.",
                            "Chaque offre detaille les conditions salariales et avantages sociaux. N'hesitez pas à les négocier lors de l'entretien."
                    )
            ),
            new IntentRule(
                    "company",
                    PRIORITY_COMPANY,
                    Set.of("culture", "equipe", "valeur", "valeurs", "mission", "environnement", "équipe", "vision", "éthique", "collaboratif", "autonomie", "innovation", "climat", "ambiance"),
                    List.of(
                            "La culture d'équipe se vérifie avec des exemples concrets: rituels, autonomie et façon de collaborer.",
                            "Posez des questions sur les valeurs, la mission et les modes de travail pendant l'entretien.",
                            "Découvrez la culture interne en visitant les bureaux, en rencontrant l'équipe et en consultant les avis des employés."
                    )
            ),
            new IntentRule(
                    "application",
                    PRIORITY_APPLICATION,
                    Set.of("candidature", "statut", "dossier", "reponse", "entretien", "réponse", "confirmation", "sélection", "classement", "convocation", "interview"),
                    List.of(
                            "Pour suivre une candidature, ouvrez votre tableau de bord et vérifiez le statut du dossier.",
                            "Si le statut n'a pas changé, contactez le recruteur avec la référence de candidature.",
                            "Les entretiens se déroulent généralement en plusieurs étapes. Un email de confirmation vous sera envoyé avant chaque rendez-vous."
                    )
            ),
            new IntentRule(
                    "support",
                    PRIORITY_SUPPORT,
                    Set.of("support", "aide", "probleme", "erreur", "contact", "bloque", "problème", "blocage", "help", "assistance", "soutien", "urgence", "signaler", "faire un signalement"),
                    List.of(
                            "Décrivez le problème, l'action effectuée et le message d'erreur. Je vous aiderai à isoler la cause.",
                            "Pour un problème technique, indiquez la page concernée, votre rôle et les étapes pour reproduire.",
                            "Contactez notre support: support@talentconnect.tn ou utilisez le formulaire de contact sur la plateforme."
                    )
            ),
            new IntentRule(
                    "goodbye",
                    PRIORITY_GOODBYE,
                    Set.of("bye", "ciao", "adieu", "revoir", "bientot", "bientôt", "au revoir", "à bientôt", "à revoir", "tchao", "bisous"),
                    List.of(
                            "Au revoir! Revenez quand vous avez besoin d'aide sur TalentConnect.",
                            "À bientôt! Bonne continuation dans vos démarches.",
                            "Merci de votre visite. À très bientôt!"
                    )
            )
    );

    public BotReply replyTo(String message) {
        String normalized = normalize(message);
        Set<String> tokens = Arrays.stream(TOKEN_SPLIT.split(normalized))
                .filter(token -> !token.isBlank())
                .collect(Collectors.toSet());

        return rules.stream()
                .map(rule -> new IntentMatch(rule, rule.matchScore(normalized, tokens)))
                .filter(match -> match.score() > 0)
                .max(Comparator.comparingInt(IntentMatch::score)
                        .thenComparingInt(match -> match.rule().priority()))
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

    private static final class IntentRule {

        private final String intent;
        private final int priority;
        private final Set<String> words;     // normalized single-word keywords
        private final List<String> phrases;  // normalized multi-word keywords
        private final List<String> responses;

        IntentRule(String intent, int priority, Set<String> keywords, List<String> responses) {
            this.intent = intent;
            this.priority = priority;
            this.responses = responses;

            Set<String> singleWords = new HashSet<>();
            List<String> multiWords = new ArrayList<>();
            for (String keyword : keywords) {
                String normalizedKeyword = normalize(keyword);
                if (normalizedKeyword.isBlank()) {
                    continue;
                }
                if (normalizedKeyword.indexOf(' ') >= 0) {
                    multiWords.add(normalizedKeyword);
                } else {
                    singleWords.add(normalizedKeyword);
                }
            }
            this.words = Set.copyOf(singleWords);
            this.phrases = List.copyOf(multiWords);
        }

        /**
         * Score = number of distinct message tokens that match this intent,
         * either exactly or via spell-tolerant fuzzy matching, plus any
         * multi-word phrase present in the message. Counting distinct tokens
         * (instead of matched keywords) prevents a single typo from being
         * counted several times when keywords overlap.
         */
        int matchScore(String normalizedMessage, Set<String> tokens) {
            int score = 0;
            for (String token : tokens) {
                for (String keyword : words) {
                    if (token.equals(keyword) || FuzzyMatcher.isFuzzyMatch(token, keyword)) {
                        score++;
                        break;
                    }
                }
            }
            for (String phrase : phrases) {
                if (normalizedMessage.contains(phrase)) {
                    score++;
                }
            }
            return score;
        }

        String intent() {
            return intent;
        }

        int priority() {
            return priority;
        }

        String selectResponse(String normalizedMessage) {
            return responses.get(indexFor(normalizedMessage, responses.size()));
        }
    }

    private record IntentMatch(IntentRule rule, int score) {
    }
}
