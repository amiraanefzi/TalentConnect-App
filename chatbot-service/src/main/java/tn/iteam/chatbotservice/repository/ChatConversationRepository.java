package tn.iteam.chatbotservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.iteam.chatbotservice.model.ChatConversation;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ChatConversationRepository extends JpaRepository<ChatConversation, Long> {

    List<ChatConversation> findByUserId(String userId);

    List<ChatConversation> findByUserIdOrderByCreatedAtDesc(String userId);

    List<ChatConversation> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

}

