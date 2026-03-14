package fr.matteofierquin.springchat.dto;

import lombok.Builder;
import lombok.With;
import java.time.Instant;

@Builder
@With
public record ChatMessageDto(
    String id,
    String content,
    String senderId,
    String senderName,
    String recipientId,
    MessageType type,
    Instant timestamp
) {
    public enum MessageType {
        CHAT,
        TYPING
    }

    public static ChatMessageDto of(String content, String senderId, String recipientId) {
        return new ChatMessageDto(
            null, content, senderId, null, recipientId, MessageType.CHAT, Instant.now()
        );
    }
}
