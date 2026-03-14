package fr.matteofierquin.springchat.service;

import fr.matteofierquin.springchat.dto.ChatMessageDto;
import fr.matteofierquin.springchat.entity.ChatMessageEntity;
import fr.matteofierquin.springchat.repository.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;

    @Transactional
    public void saveMessage(ChatMessageDto chatMessage) {
        ChatMessageEntity messageEntity = ChatMessageEntity.builder()
                .content(chatMessage.content())
                .senderId(chatMessage.senderId())
                .senderName(chatMessage.senderName())
                .recipientId(chatMessage.recipientId())
                .messageType(chatMessage.type().name())
                .build();

        chatMessageRepository.save(messageEntity);
    }

    @Transactional(readOnly = true)
    public List<ChatMessageDto> getConversation(String userId1, String userId2) {
        return chatMessageRepository.findBySenderIdAndRecipientIdOrderByCreatedAtAsc(userId1, userId2)
                .stream()
                .map(this::toChatMessageDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ChatMessageDto> getMessagesForUser(String userId) {
        return chatMessageRepository.findByRecipientIdOrderByCreatedAtAsc(userId)
                .stream()
                .map(this::toChatMessageDto)
                .toList();
    }

    private ChatMessageDto toChatMessageDto(ChatMessageEntity entity) {
        return ChatMessageDto.builder()
                .id(entity.getId())
                .content(entity.getContent())
                .senderId(entity.getSenderId())
                .senderName(entity.getSenderName())
                .recipientId(entity.getRecipientId())
                .type(ChatMessageDto.MessageType.valueOf(entity.getMessageType()))
                .timestamp(entity.getCreatedAt())
                .build();
    }
}