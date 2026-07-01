package com.talentconnect.candidatures.application;

import com.talentconnect.candidatures.domain.Notification;
import com.talentconnect.candidatures.dto.NotificationResponse;
import com.talentconnect.candidatures.exception.ForbiddenException;
import com.talentconnect.candidatures.exception.ResourceNotFoundException;
import com.talentconnect.candidatures.infrastructure.jpa.NotificationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class NotificationService {

    /** userId spécial : canal broadcast RH (toutes les notifications pour tous les RH) */
    public static final long RH_BROADCAST_USER_ID = 0L;

    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    /** Retourne les notifs personnelles. Si role = ROLE_RH, inclut aussi le broadcast RH (userId=0). */
    public List<NotificationResponse> findForUser(Long userId, String role) {
        if (isRh(role)) {
            return notificationRepository.findForUserIncludingBroadcast(userId)
                    .stream().map(NotificationResponse::from).toList();
        }
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream().map(NotificationResponse::from).toList();
    }

    /** Compte les non lues. Si role = ROLE_RH, inclut le broadcast RH. */
    public long countUnread(Long userId, String role) {
        if (isRh(role)) {
            return notificationRepository.countUnreadIncludingBroadcast(userId);
        }
        return notificationRepository.countByUserIdAndReadFalse(userId);
    }

    @Transactional
    public NotificationResponse markRead(Long id, Long userId, String role) {
        Notification n = notificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notification introuvable: " + id));
        // Autorisé si : propriétaire OU RH pour les broadcasts (userId=0)
        boolean isOwner      = n.getUserId().equals(userId);
        boolean isRhBroadcast = isRh(role) && n.getUserId() == RH_BROADCAST_USER_ID;
        if (!isOwner && !isRhBroadcast) throw new ForbiddenException("Acces interdit");
        n.markRead();
        return NotificationResponse.from(notificationRepository.save(n));
    }

    @Transactional
    public void markAllRead(Long userId, String role) {
        if (isRh(role)) {
            notificationRepository.markAllReadIncludingBroadcast(userId);
        } else {
            notificationRepository.markAllReadByUserId(userId);
        }
    }

    @Transactional
    public void delete(Long id, Long userId, String role) {
        Notification n = notificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notification introuvable: " + id));
        boolean isOwner      = n.getUserId().equals(userId);
        boolean isRhBroadcast = isRh(role) && n.getUserId() == RH_BROADCAST_USER_ID;
        if (!isOwner && !isRhBroadcast) throw new ForbiddenException("Acces interdit");
        notificationRepository.deleteById(id);
    }

    /** Méthode interne : push une notification à un utilisateur spécifique */
    @Transactional
    public void push(Long userId, Notification.NotifType type, String title, String message, String deepLink) {
        notificationRepository.save(new Notification(userId, type, title, message, deepLink));
    }

    /** Méthode interne : push une notification broadcast vers tous les RH (userId = 0) */
    @Transactional
    public void pushToRh(Notification.NotifType type, String title, String message, String deepLink) {
        notificationRepository.save(new Notification(RH_BROADCAST_USER_ID, type, title, message, deepLink));
    }

    private boolean isRh(String role) {
        return role != null && (role.equals("ROLE_RH") || role.equals("RH"));
    }
}

