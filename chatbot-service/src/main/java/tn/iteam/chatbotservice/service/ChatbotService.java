package tn.iteam.chatbotservice.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
    private final ChatConversationRepository conversationRepository;

    public ChatbotService(ChatbotEngine chatbotEngine,
                          JobSearchHandler jobSearchHandler,
                          ChatConversationRepository conversationRepository) {
        this.chatbotEngine = chatbotEngine;
        this.jobSearchHandler = jobSearchHandler;
        this.conversationRepository = conversationRepository;
    }

    @Transactional
    public ChatResponse reply(ChatRequest request) {
        // Sauvegarder le message utilisateur
        conversationRepository.save(ChatConversation.create(request.userId(), ChatSender.USER, request.message()));

        // 1. Essayer d'abord le handler basé sur les VRAIES données (job-service DB)
        String jobAnswer = jobSearchHandler.handle(request.message());

        final String responseText;
        final String intent;

        if (jobAnswer != null) {
            responseText = jobAnswer;
            intent = "job_search_live";
        } else {
            // 2. Fallback sur les réponses statiques du ChatbotEngine
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
