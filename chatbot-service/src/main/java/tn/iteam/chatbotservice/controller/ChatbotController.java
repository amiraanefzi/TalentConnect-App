package tn.iteam.chatbotservice.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tn.iteam.chatbotservice.dto.ChatRequest;
import tn.iteam.chatbotservice.dto.ChatResponse;
import tn.iteam.chatbotservice.dto.ConversationResponse;
import tn.iteam.chatbotservice.dto.HealthResponse;
import tn.iteam.chatbotservice.service.ChatbotService;

import java.time.Instant;
import java.util.List;

@Validated
@RestController
@RequestMapping("/api/chatbot")
@CrossOrigin(origins = "${chatbot.cors.allowed-origins:http://localhost:4200}")
public class ChatbotController {

    private final ChatbotService chatbotService;

    public ChatbotController(ChatbotService chatbotService) {
        this.chatbotService = chatbotService;
    }

    @PostMapping("/messages")
    public ResponseEntity<ChatResponse> chat(@Valid @RequestBody ChatRequest request) {
        return ResponseEntity.ok(chatbotService.reply(request));
    }

    @GetMapping("/history/{userId}")
    public ResponseEntity<Page<ConversationResponse>> history(
            @PathVariable @NotBlank String userId,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size) {
        return ResponseEntity.ok(chatbotService.history(userId, page, size));
    }

    @GetMapping("/recent/{userId}")
    public ResponseEntity<List<ConversationResponse>> recent(
            @PathVariable @NotBlank String userId,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int limit) {
        return ResponseEntity.ok(chatbotService.recent(userId, limit));
    }

    @DeleteMapping("/history/{userId}")
    public ResponseEntity<Void> clearHistory(@PathVariable @NotBlank String userId) {
        chatbotService.clearHistory(userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/health")
    public ResponseEntity<HealthResponse> health() {
        return ResponseEntity.ok(new HealthResponse("UP", "chatbot-service", Instant.now()));
    }
}
