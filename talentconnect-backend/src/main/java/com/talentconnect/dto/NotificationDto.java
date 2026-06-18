package com.talentconnect.dto;
import com.talentconnect.entity.Notification;
import java.time.LocalDateTime;
public record NotificationDto(Long id,Long userId,Notification.NotifType type,String title,String message,boolean read,String deepLink,LocalDateTime createdAt){
    public static NotificationDto from(Notification n){return new NotificationDto(n.getId(),n.getUser().getId(),n.getType(),n.getTitle(),n.getMessage(),n.isRead(),n.getDeepLink(),n.getCreatedAt());}
}
