package com.talentconnect.candidatures.domain;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "notifications")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    private NotifType type;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String message;

    @Column(name = "is_read", nullable = false)
    private boolean read = false;

    @Column(name = "deep_link", length = 500)
    private String deepLink;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected Notification() {}

    public Notification(Long userId, NotifType type, String title, String message, String deepLink) {
        this.userId    = userId;
        this.type      = type;
        this.title     = title;
        this.message   = message;
        this.deepLink  = deepLink;
        this.createdAt = Instant.now();
    }

    public Long getId()          { return id; }
    public Long getUserId()      { return userId; }
    public NotifType getType()   { return type; }
    public String getTitle()     { return title; }
    public String getMessage()   { return message; }
    public boolean isRead()      { return read; }
    public String getDeepLink()  { return deepLink; }
    public Instant getCreatedAt(){ return createdAt; }
    public void markRead()       { this.read = true; }

    public enum NotifType { SUCCESS, INFO, WARNING, ERROR }
}

