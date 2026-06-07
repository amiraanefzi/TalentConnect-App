package tn.iteam.chatbotservice.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.iteam.chatbotservice.engine.ConversationEngine;
import tn.iteam.chatbotservice.model.ChatConversation;
import tn.iteam.chatbotservice.repository.ChatConversationRepository;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class ChatbotService {

    @Autowired
    private ConversationEngine conversationEngine;

    @Autowired
    private ChatConversationRepository chatConversationRepository;

    /**
     * Process user message and generate bot response
     */
    public String processMessage(String userMessage, String userId) {
        try {
            // Save user message to database
            saveChatConversation(userMessage, "user", userId);

            // Generate bot response using conversation engine
            String botResponse = conversationEngine.processMessage(userMessage);

            // Save bot response to database
            saveChatConversation(botResponse, "bot", userId);

            log.info("Processed message from user {}: {}", userId, userMessage);
            return botResponse;
        } catch (Exception e) {
            log.error("Error processing message", e);
            return "Désolé, une erreur s'est produite. Veuillez réessayer.";
        }
    }

    /**
     * Save chat conversation to database
     */
    private void saveChatConversation(String message, String sender, String userId) {
        try {
            ChatConversation conversation = ChatConversation.builder()
                    .userId(userId)
                    .message(message)
                    .sender(sender)
                    .build();

            chatConversationRepository.save(conversation);
        } catch (Exception e) {
            log.error("Error saving conversation", e);
        }
    }

    /**
     * Get conversation history for a user
     */
    public List<ChatConversation> getConversationHistory(String userId) {
        return chatConversationRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    /**
     * Get conversation history for a user with limit
     */
    public List<ChatConversation> getRecentConversation(String userId, int limit) {
        List<ChatConversation> conversations = chatConversationRepository.findByUserIdOrderByCreatedAtDesc(userId);
        return conversations.size() > limit ? conversations.subList(0, limit) : conversations;
    }

    /**
     * Clear conversation history for a user
     */
    public void clearConversationHistory(String userId) {
        List<ChatConversation> conversations = chatConversationRepository.findByUserId(userId);
        chatConversationRepository.deleteAll(conversations);
        log.info("Cleared conversation history for user {}", userId);
    }

}

