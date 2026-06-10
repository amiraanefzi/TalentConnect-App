package tn.iteam.chatbotservice.dto;

import java.time.LocalDateTime;

public record ChatResponse(
        String userId,
        String message,
        String response,
        String intent,
        LocalDateTime createdAt
) {
}
