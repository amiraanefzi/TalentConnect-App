package com.talentconnect.candidatures.dto;

import com.talentconnect.candidatures.domain.Notification;
import java.time.Instant;

public record NotificationResponse(
        Long id,
        Long userId,
        Notification.NotifType type,
        String title,
        String message,
        boolean read,
        String deepLink,
        Instant createdAt
) {
    public static NotificationResponse from(Notification n) {
        return new NotificationResponse(
                n.getId(), n.getUserId(), n.getType(),
                n.getTitle(), n.getMessage(), n.isRead(),
                n.getDeepLink(), n.getCreatedAt()
        );
    }
}

