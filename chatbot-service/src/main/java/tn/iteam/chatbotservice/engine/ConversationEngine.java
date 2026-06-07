package tn.iteam.chatbotservice.engine;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.util.*;
import java.util.regex.Pattern;

@Slf4j
@Component
public class ConversationEngine {

    private final Map<String, ConversationPattern> patterns;

    public ConversationEngine() {
        patterns = initializePatterns();
    }

    public String processMessage(String userMessage) {
        String normalizedMessage = userMessage.toLowerCase().trim();

        for (ConversationPattern pattern : patterns.values()) {
            if (pattern.matches(normalizedMessage)) {
                return pattern.getRandomResponse();
            }
        }

        // Default response if no pattern matches
        return getDefaultResponse();
    }

    private Map<String, ConversationPattern> initializePatterns() {
        Map<String, ConversationPattern> patternsMap = new LinkedHashMap<>();

        // Greetings
        patternsMap.put("greeting", ConversationPattern.builder()
                .keywords(Arrays.asList("bonjour", "salut", "coucou", "hello", "hi"))
                .responses(Arrays.asList(
                    "Bonjour! 👋 Bienvenue sur TalentConnect. Comment puis-je vous aider aujourd'hui ?",
                    "Salut! 😊 Je suis ici pour vous assister. Que puis-je faire pour vous ?",
                    "Bienvenue! Comment puis-je vous aider dans votre parcours professionnel ?"
                ))
                .build());

        // Job Search
        patternsMap.put("job_search", ConversationPattern.builder()
                .keywords(Arrays.asList("emploi", "job", "poste", "offre", "recherche"))
                .responses(Arrays.asList(
                    "Vous cherchez un emploi? 💼 Nous avons de nombreuses offres disponibles. Dites-moi quel type de poste vous intéresse!",
                    "Intéressé par une nouvelle opportunité? 🚀 Je peux vous aider à trouver le poste idéal.",
                    "Quel domaine d'activité vous intéresse?"
                ))
                .build());

        // Career Development
        patternsMap.put("career", ConversationPattern.builder()
                .keywords(Arrays.asList("carrière", "développement", "compétence", "formation", "évolution"))
                .responses(Arrays.asList(
                    "Le développement de carrière est important! 📈 Nous offrons plusieurs formations. Quel domaine vous intéresse ?",
                    "Vous souhaitez évoluer professionnellement? Je peux vous proposer des ressources de formation.",
                    "Le développement continu des compétences est clé. Que souhaitez-vous apprendre?"
                ))
                .build());

        // Benefits
        patternsMap.put("benefits", ConversationPattern.builder()
                .keywords(Arrays.asList("avantages", "salaire", "prime", "bonus", "assurance", "congés"))
                .responses(Arrays.asList(
                    "Notre package d'avantages est très compétitif! 💰 Nous proposons des primes, assurances, et plus. Plusieurs options sont disponibles.",
                    "Vous avez des questions sur nos avantages? 🎁 Nous offrons de nombreux bénéfices attractifs.",
                    "Les avantages sociaux incluent assurance, primes et congés. Besoin de plus de détails?"
                ))
                .build());

        // Company Culture
        patternsMap.put("culture", ConversationPattern.builder()
                .keywords(Arrays.asList("culture", "environnement", "équipe", "valeurs", "mission"))
                .responses(Arrays.asList(
                    "Notre culture d'entreprise valorise l'innovation et la collaboration! 🤝",
                    "Nous croyons en un environnement inclusif et dynamique où chacun peut s'épanouir.",
                    "L'équipe est au cœur de notre mission. Nous valorisons la diversité et l'entraide."
                ))
                .build());

        // Application Status
        patternsMap.put("application", ConversationPattern.builder()
                .keywords(Arrays.asList("candidature", "application", "statut", "dossier", "réponse"))
                .responses(Arrays.asList(
                    "Pour connaître le statut de votre candidature, veuillez consulter votre tableau de bord ou me fournir votre ID de candidature. 📋",
                    "Vous suivez une candidature? Vous pouvez vérifier son statut dans votre profil.",
                    "J'aimerais vous aider! Pouvez-vous me donner votre numéro de candidature?"
                ))
                .build());

        // Contact & Support
        patternsMap.put("contact", ConversationPattern.builder()
                .keywords(Arrays.asList("contact", "support", "aide", "problème", "error"))
                .responses(Arrays.asList(
                    "Vous avez besoin d'aide? Notre équipe support est disponible. 📞 Vous pouvez nous contacter à support@talentconnect.com",
                    "Un problème technique? N'hésitez pas à nous contacter via le formulaire de contact.",
                    "Je suis là pour aider! Si vous avez un problème spécifique, dites-moi et je trouverai une solution."
                ))
                .build());

        // Goodbye
        patternsMap.put("goodbye", ConversationPattern.builder()
                .keywords(Arrays.asList("au revoir", "bye", "à bientôt", "ciao", "adieu"))
                .responses(Arrays.asList(
                    "Au revoir! Bonne chance dans votre parcours! 👋",
                    "À bientôt! N'hésitez pas à revenir si vous avez des questions.",
                    "Merci d'avoir discuté avec moi. À bientôt! 😊"
                ))
                .build());

        return patternsMap;
    }

    private String getDefaultResponse() {
        String[] responses = {
            "Je n'ai pas bien compris. Pourriez-vous reformuler votre question? 🤔",
            "Intéressant! Pouvez-vous me donner plus de détails?",
            "Je peux peut-être vous aider mieux si vous me précisiez ce que vous cherchez.",
            "Parlez-moi davantage. Que puis-je faire pour vous?"
        };
        Random rand = new Random();
        return responses[rand.nextInt(responses.length)];
    }

}

