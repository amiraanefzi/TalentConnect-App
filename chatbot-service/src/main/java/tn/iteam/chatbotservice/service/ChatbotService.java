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
import tn.iteam.chatbotservice.repository.ChatConversationRepository;

import java.util.List;

@Service
public class ChatbotService {

    private final ChatbotEngine chatbotEngine;
    private final ChatConversationRepository conversationRepository;

    public ChatbotService(ChatbotEngine chatbotEngine, ChatConversationRepository conversationRepository) {
        this.chatbotEngine = chatbotEngine;
        this.conversationRepository = conversationRepository;
    }

    @Transactional
    public ChatResponse reply(ChatRequest request) {
        conversationRepository.save(ChatConversation.create(request.userId(), ChatSender.USER, request.message()));

        BotReply reply = chatbotEngine.replyTo(request.message());
        ChatConversation botMessage = conversationRepository.save(
                ChatConversation.create(request.userId(), ChatSender.BOT, reply.message())
        );

        return new ChatResponse(
                request.userId(),
                request.message(),
                reply.message(),
                reply.intent(),
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
