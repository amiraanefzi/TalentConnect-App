package tn.iteam.chatbotservice.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Entity
@Table(
        name = "chat_conversations",
        indexes = {
                @Index(name = "idx_chat_user_created_at", columnList = "user_id,created_at"),
                @Index(name = "idx_chat_created_at", columnList = "created_at")
        }
)
public class ChatConversation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false, length = 100)
    private String userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ChatSender sender;

    @Column(nullable = false, length = 2000)
    private String message;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    protected ChatConversation() {
    }

    private ChatConversation(String userId, ChatSender sender, String message, LocalDateTime createdAt) {
        this.userId = userId;
        this.sender = sender;
        this.message = message;
        this.createdAt = createdAt;
    }

    public static ChatConversation create(String userId, ChatSender sender, String message) {
        return new ChatConversation(userId, sender, message, LocalDateTime.now(ZoneOffset.UTC));
    }

    public Long getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public ChatSender getSender() {
        return sender;
    }

    public String getMessage() {
        return message;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
