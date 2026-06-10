package tn.iteam.chatbotservice.dto;

import tn.iteam.chatbotservice.domain.ChatConversation;
import tn.iteam.chatbotservice.domain.ChatSender;

import java.time.LocalDateTime;

public record ConversationResponse(
        Long id,
        String userId,
        ChatSender sender,
        String message,
        LocalDateTime createdAt
) {

    public static ConversationResponse from(ChatConversation conversation) {
        return new ConversationResponse(
                conversation.getId(),
                conversation.getUserId(),
                conversation.getSender(),
                conversation.getMessage(),
                conversation.getCreatedAt()
        );
    }
}
