package fr.matteofierquin.springchat.repository;

import fr.matteofierquin.springchat.entity.ChatMessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessageEntity, String> {
    List<ChatMessageEntity> findBySenderIdAndRecipientIdOrderByCreatedAtAsc(String senderId, String recipientId);
    List<ChatMessageEntity> findBySenderIdAndRecipientIdOrderByCreatedAtDesc(String senderId, String recipientId);
    
    List<ChatMessageEntity> findByRecipientIdOrderByCreatedAtAsc(String recipientId);
    List<ChatMessageEntity> findByRecipientIdOrderByCreatedAtDesc(String recipientId);
}