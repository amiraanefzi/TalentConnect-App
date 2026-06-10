package tn.iteam.chatbotservice.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import tn.iteam.chatbotservice.domain.ChatConversation;

public interface ChatConversationRepository extends JpaRepository<ChatConversation, Long> {

    Page<ChatConversation> findByUserIdOrderByCreatedAtDesc(String userId, Pageable pageable);

    long deleteByUserId(String userId);
}
