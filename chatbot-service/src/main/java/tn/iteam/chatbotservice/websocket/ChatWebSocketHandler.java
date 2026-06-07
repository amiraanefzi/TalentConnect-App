package tn.iteam.chatbotservice.websocket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import tn.iteam.chatbotservice.dto.ChatMessage;
import tn.iteam.chatbotservice.service.ChatbotService;

import java.io.IOException;

@Slf4j
@Component
public class ChatWebSocketHandler extends TextWebSocketHandler {

    @Autowired
    private ChatbotService chatbotService;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ConnectionManager connectionManager = new ConnectionManager();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        connectionManager.addSession(session);
        log.info("WebSocket connection established: {}", session.getId());

        // Send welcome message
        ChatMessage welcomeMessage = ChatMessage.builder()
                .sender("bot")
                .message("Bonjour! Je suis votre assistant RH TalentConnect. Comment puis-je vous aider ?")
                .timestamp(System.currentTimeMillis())
                .build();

        session.sendMessage(new TextMessage(objectMapper.writeValueAsString(welcomeMessage)));
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        try {
            ChatMessage chatMessage = objectMapper.readValue(message.getPayload(), ChatMessage.class);
            chatMessage.setTimestamp(System.currentTimeMillis());

            log.info("Received message from {}: {}", session.getId(), chatMessage.getMessage());

            // Get bot response
            String botResponse = chatbotService.processMessage(chatMessage.getMessage(),
                                                              chatMessage.getUserId());

            ChatMessage responseMessage = ChatMessage.builder()
                    .sender("bot")
                    .message(botResponse)
                    .timestamp(System.currentTimeMillis())
                    .build();

            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(responseMessage)));
        } catch (Exception e) {
            log.error("Error handling message", e);
            ChatMessage errorMessage = ChatMessage.builder()
                    .sender("bot")
                    .message("Désolé, une erreur s'est produite. Veuillez réessayer.")
                    .timestamp(System.currentTimeMillis())
                    .build();
            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(errorMessage)));
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        connectionManager.removeSession(session);
        log.info("WebSocket connection closed: {}", session.getId());
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        log.error("WebSocket error: {}", session.getId(), exception);
    }

}

