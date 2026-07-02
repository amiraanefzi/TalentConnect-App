package tn.iteam.chatbotservice.service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.iteam.chatbotservice.client.auth.AuthClient;
import tn.iteam.chatbotservice.client.auth.UserSummary;
import tn.iteam.chatbotservice.client.candidatures.CandidatureClient;
import tn.iteam.chatbotservice.client.candidatures.CandidatureSummary;
import tn.iteam.chatbotservice.domain.ChatConversation;
import tn.iteam.chatbotservice.domain.ChatSender;
import tn.iteam.chatbotservice.dto.ChatRequest;
import tn.iteam.chatbotservice.dto.ChatResponse;
import tn.iteam.chatbotservice.dto.ConversationResponse;
import tn.iteam.chatbotservice.engine.BotReply;
import tn.iteam.chatbotservice.engine.ChatbotEngine;
import tn.iteam.chatbotservice.engine.JobSearchHandler;
import tn.iteam.chatbotservice.repository.ChatConversationRepository;
import java.util.List;
@Service
public class ChatbotService {
    private final ChatbotEngine chatbotEngine;
    private final JobSearchHandler jobSearchHandler;
    private final AuthClient authClient;
    private final CandidatureClient candidatureClient;
    private final ChatConversationRepository conversationRepository;
    public ChatbotService(ChatbotEngine chatbotEngine,
                          JobSearchHandler jobSearchHandler,
                          AuthClient authClient,
                          CandidatureClient candidatureClient,
                          ChatConversationRepository conversationRepository) {
        this.chatbotEngine = chatbotEngine;
        this.jobSearchHandler = jobSearchHandler;
        this.authClient = authClient;
        this.candidatureClient = candidatureClient;
        this.conversationRepository = conversationRepository;
    }
    @Transactional
    public ChatResponse reply(ChatRequest request) {
        // Sauvegarder le message utilisateur
        conversationRepository.save(ChatConversation.create(request.userId(), ChatSender.USER, request.message()));
        String userIdStr = request.userId();
        Long numericUserId = null;
        if (userIdStr != null && userIdStr.startsWith("user-")) {
            try {
                numericUserId = Long.parseLong(userIdStr.substring(5));
            } catch (NumberFormatException ignored) {}
        }
        String responseText = null;
        String intent = "fallback";
        // 1. Analyse spcifique au contexte utilisateur (Profil / Candidatures)
        if (numericUserId != null) {
            String contextResponse = handleUserContext(request.message(), numericUserId);
            if (contextResponse != null) {
                responseText = contextResponse;
                intent = "user_context";
            }
        }
        // 2. Si pas de contexte utilisateur, essayer de chercher des jobs
        if (responseText == null) {
            String jobAnswer = jobSearchHandler.handle(request.message());
            if (jobAnswer != null) {
                responseText = jobAnswer;
                intent = "job_search_live";
            }
        }
        // 3. Fallback sur les rponses statiques intelligentes
        if (responseText == null) {
            BotReply reply = chatbotEngine.replyTo(request.message());
            responseText = reply.message();
            intent = reply.intent();
        }
        ChatConversation botMessage = conversationRepository.save(
                ChatConversation.create(request.userId(), ChatSender.BOT, responseText)
        );
        return new ChatResponse(
                request.userId(),
                request.message(),
                responseText,
                intent,
                botMessage.getCreatedAt()
        );
    }
    private String handleUserContext(String message, Long userId) {
        if (message == null) return null;
        String msg = message.toLowerCase();
        // Candidatures
        if (msg.contains("ma candidature") || msg.contains("mes candidatures") || msg.contains("mon statut")) {
            List<CandidatureSummary> candidatures = candidatureClient.getUserCandidatures(userId);
            if (candidatures == null || candidatures.isEmpty()) {
                return "Vous n'avez pas encore de candidature en cours sur TalentConnect. Souhaitez-vous consulter nos offres d'emploi ?";
            }
            StringBuilder sb = new StringBuilder("Voici le statut de vos candidatures actuelles :\n\n");
            for (CandidatureSummary c : candidatures) {
                sb.append("• Candidature #").append(c.id())
                  .append(" | Statut : ").append(c.status())
                  .append("\n");
            }
            return sb.toString();
        }
        // Profil / Comptences
        if (msg.contains("mon profil") || msg.contains("mes comptences") || msg.contains("qui suis-je")) {
            UserSummary user = authClient.getUserProfile(userId);
            if (user == null) return null;
            StringBuilder sb = new StringBuilder("Voici les informations de votre profil professionnel :\n\n")
                .append("Nom : ").append(user.lastName()).append(" ").append(user.firstName()).append("\n")
                .append("Dpartement : ").append(user.department() != null ? user.department() : "Non renseign").append("\n")
                .append("Poste : ").append(user.title() != null ? user.title() : "Non renseign").append("\n");
            if (user.skills() != null && !user.skills().isEmpty()) {
                sb.append("Comptences : ").append(String.join(", ", user.skills())).append("\n");
            }
            return sb.toString();
        }
        return null;
    }
    @Transactional(readOnly = true)
    public Page<ConversationResponse> history(String userId, int page, int size) {
        return conversationRepository.findByUserIdOrderByCreatedAtDesc(userId, PageRequest.of(page, size))
                .map(ConversationResponse::from);
    }
    @Transactional(readOnly = true)
    public List<ConversationResponse> recent(String userId, int limit) {
        return history(userId, 0, limit).getContent();
    }
    @Transactional
    public long clearHistory(String userId) {
        return conversationRepository.deleteByUserId(userId);
    }
}
