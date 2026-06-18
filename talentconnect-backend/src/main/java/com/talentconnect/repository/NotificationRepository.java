package com.talentconnect.repository;
import com.talentconnect.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
public interface NotificationRepository extends JpaRepository<Notification,Long> {
    List<Notification> findByUserIdOrderByCreatedAtDesc(Long userId);
    List<Notification> findByUserIdAndReadFalse(Long userId);
    @Modifying @Query("UPDATE Notification n SET n.read=true WHERE n.user.id=:userId")
    void markAllReadByUserId(@Param("userId") Long userId);
}
