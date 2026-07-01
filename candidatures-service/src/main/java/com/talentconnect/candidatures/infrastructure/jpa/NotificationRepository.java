package com.talentconnect.candidatures.infrastructure.jpa;

import com.talentconnect.candidatures.domain.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByUserIdOrderByCreatedAtDesc(Long userId);

    /** Récupère les notifications pour userId OU les broadcasts (userId = 0) */
    @Query("SELECT n FROM Notification n WHERE n.userId = :userId OR n.userId = 0 ORDER BY n.createdAt DESC")
    List<Notification> findForUserIncludingBroadcast(@Param("userId") Long userId);

    long countByUserIdAndReadFalse(Long userId);

    /** Compte les non lues pour l'utilisateur ET les broadcasts non lus */
    @Query("SELECT COUNT(n) FROM Notification n WHERE (n.userId = :userId OR n.userId = 0) AND n.read = false")
    long countUnreadIncludingBroadcast(@Param("userId") Long userId);

    @Modifying
    @Query("UPDATE Notification n SET n.read = true WHERE n.userId = :userId")
    void markAllReadByUserId(@Param("userId") Long userId);

    /** Marque comme lues toutes les notifs de l'utilisateur ET les broadcasts */
    @Modifying
    @Query("UPDATE Notification n SET n.read = true WHERE n.userId = :userId OR n.userId = 0")
    void markAllReadIncludingBroadcast(@Param("userId") Long userId);
}

