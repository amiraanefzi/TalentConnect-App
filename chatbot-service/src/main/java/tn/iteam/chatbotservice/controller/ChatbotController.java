package tn.iteam.chatbotservice.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.iteam.chatbotservice.model.ChatConversation;
import tn.iteam.chatbotservice.service.ChatbotService;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/chatbot")
@CrossOrigin(origins = "*")
public class ChatbotController {

    @Autowired
    private ChatbotService chatbotService;

    /**
     * Get conversation history for a user
     */
    @GetMapping("/history/{userId}")
    public ResponseEntity<List<ChatConversation>> getConversationHistory(@PathVariable String userId) {
        try {
            List<ChatConversation> history = chatbotService.getConversationHistory(userId);
            return ResponseEntity.ok(history);
        } catch (Exception e) {
            log.error("Error fetching conversation history", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get recent conversation
     */
    @GetMapping("/recent/{userId}")
    public ResponseEntity<List<ChatConversation>> getRecentConversation(
            @PathVariable String userId,
            @RequestParam(defaultValue = "10") int limit) {
        try {
            List<ChatConversation> recent = chatbotService.getRecentConversation(userId, limit);
            return ResponseEntity.ok(recent);
        } catch (Exception e) {
            log.error("Error fetching recent conversation", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Clear conversation history for a user
     */
    @DeleteMapping("/history/{userId}")
    public ResponseEntity<String> clearHistory(@PathVariable String userId) {
        try {
            chatbotService.clearConversationHistory(userId);
            return ResponseEntity.ok("Conversation history cleared");
        } catch (Exception e) {
            log.error("Error clearing conversation history", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Chatbot Service is running");
    }

}

