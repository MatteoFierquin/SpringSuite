package fr.matteofierquin.springchat.controller;

import fr.matteofierquin.springchat.dto.ChatMessageDto;
import fr.matteofierquin.springchat.service.ChatMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final ChatMessageService chatMessageService;
    private final SimpMessageSendingOperations messagingTemplate;

    @MessageMapping("/message.send.{recipientId}")
    public void sendMessage(
            @DestinationVariable String recipientId,
            @Payload ChatMessageDto chatMessage,
            SimpMessageHeaderAccessor headerAccessor) {

        Map<String, Object> sessionAttributes = headerAccessor.getSessionAttributes();
        String userId = (String) sessionAttributes.get("userId");
        String username = (String) sessionAttributes.get("username");

        ChatMessageDto message = chatMessage
                .withId(UUID.randomUUID().toString())
                .withSenderId(userId)
                .withSenderName(username)
                .withRecipientId(recipientId)
                .withType(ChatMessageDto.MessageType.CHAT)
                .withTimestamp(Instant.now());

        chatMessageService.saveMessage(message);

        // Send to recipient's queue
        messagingTemplate.convertAndSendToUser(recipientId, "/queue/messages", message);
        
        // Also send back to sender for confirmation
        messagingTemplate.convertAndSendToUser(userId, "/queue/messages", message);
    }

    @MessageMapping("/typing.{recipientId}")
    public void sendTypingStatus(
            @DestinationVariable String recipientId,
            SimpMessageHeaderAccessor headerAccessor) {

        Map<String, Object> sessionAttributes = headerAccessor.getSessionAttributes();
        String userId = (String) sessionAttributes.get("userId");
        String username = (String) sessionAttributes.get("username");

        ChatMessageDto typingMessage = ChatMessageDto.builder()
                .id(UUID.randomUUID().toString())
                .senderId(userId)
                .senderName(username)
                .recipientId(recipientId)
                .type(ChatMessageDto.MessageType.TYPING)
                .timestamp(Instant.now())
                .build();

        messagingTemplate.convertAndSendToUser(recipientId, "/queue/typing", typingMessage);
    }
}