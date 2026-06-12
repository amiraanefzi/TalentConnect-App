package tn.iteam.chatbotservice;

import tn.iteam.chatbotservice.engine.ChatbotEngine;
import tn.iteam.chatbotservice.engine.BotReply;
import java.util.Scanner;

/**
 * Wrapper interactif pour tester le chatbot en ligne de commande (moteur seul, sans base de donnees).
 * Compilez puis lancez avec:
 *   ./mvnw -o test-compile
 *   java -cp "target/test-classes;target/classes" tn.iteam.chatbotservice.InteractiveChatbotRunner
 */
public class InteractiveChatbotRunner {

    public static void main(String[] args) {
        ChatbotEngine engine = new ChatbotEngine();

        System.out.println("\n");
        System.out.println("╔════════════════════════════════════════════════════╗");
        System.out.println("║  CHATBOT TALENTCONNECT - TEST INTERACTIF          ║");
        System.out.println("║  Testez la tolérance aux fautes d'orthographe    ║");
        System.out.println("╚════════════════════════════════════════════════════╝");
        System.out.println();

        printInstructions();

        Scanner scanner = new Scanner(System.in);
        String input;

        while (true) {
            System.out.print("\n💬 Votre message (ou 'quit' pour quitter): ");
            input = scanner.nextLine().trim();

            if (input.equalsIgnoreCase("quit") || input.equalsIgnoreCase("exit")) {
                System.out.println("\n👋 Au revoir!");
                break;
            }

            if (input.isEmpty()) {
                System.out.println("⚠️  Message vide, veuillez saisir quelque chose.");
                continue;
            }

            test(engine, input);
        }

        scanner.close();
    }

    private static void test(ChatbotEngine engine, String message) {
        System.out.println("\n📤 Message: \"" + message + "\"");

        BotReply reply = engine.replyTo(message);

        System.out.println("🎯 Intention détectée: " + reply.intent());
        System.out.println("💬 Réponse: \"" + reply.message() + "\"");

        printDetails(message, reply);
    }

    private static void printDetails(String message, BotReply reply) {
        System.out.println("\n📊 Détails:");
        System.out.println("   • Intention: " + colorIntent(reply.intent()));
        System.out.println("   • Longueur message: " + message.length() + " caractères");
        System.out.println("   • Mots: " + message.split("\\s+").length);

        if ("fallback".equals(reply.intent())) {
            System.out.println("   ⚠️  Aucune correspondance trouvée.");
            System.out.println("   Essayez: offre, emploi, carrière, salaire, support, etc.");
        } else {
            System.out.println("   ✅ Message bien compris!");
        }
    }

    private static String colorIntent(String intent) {
        return switch (intent) {
            case "greeting" -> "👋 Salutation";
            case "job_search" -> "💼 Recherche d'emploi";
            case "career" -> "📈 Carrière";
            case "benefits" -> "💰 Avantages/Salaire";
            case "company" -> "🏢 Culture d'entreprise";
            case "application" -> "📋 Candidature";
            case "support" -> "🆘 Support";
            case "goodbye" -> "👋 Au revoir";
            default -> "❓ " + intent;
        };
    }

    private static void printInstructions() {
        System.out.println("📖 INSTRUCTIONS:");
        System.out.println();
        System.out.println("Testez le chatbot en écrivant des messages, même mal orthographiés.");
        System.out.println("Le chatbot reconnaîtra les intentions grâce au fuzzy matching.");
        System.out.println();
        System.out.println("Exemples de messages à tester:");
        System.out.println("  ✓ bonjour");
        System.out.println("  ✓ bonjourr (avec typo)");
        System.out.println("  ✓ je cherche un emploi");
        System.out.println("  ✓ je cherche un empoloi (avec typo)");
        System.out.println("  ✓ quel est le salaire?");
        System.out.println("  ✓ kel est le salaira? (avec typos)");
        System.out.println("  ✓ j'ai besoin de support");
        System.out.println("  ✓ j'ai besoin de suport (avec typo)");
        System.out.println("  ✓ Au revoir!");
        System.out.println();
        System.out.println("Tapez 'quit' ou 'exit' pour quitter.");
        System.out.println();
    }
}
