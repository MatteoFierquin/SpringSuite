package fr.matteofierquin.springchat.service;

import fr.matteofierquin.springchat.dto.ChatMessageDto;
import fr.matteofierquin.springchat.entity.ChatMessageEntity;
import fr.matteofierquin.springchat.repository.ChatMessageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("ChatMessageService Unit Tests")
class ChatMessageServiceTest {

    @Mock
    private ChatMessageRepository chatMessageRepository;

    @InjectMocks
    private ChatMessageService chatMessageService;

    private ChatMessageDto testMessage;
    private ChatMessageEntity savedEntity;

    @BeforeEach
    void setUp() {
        testMessage = ChatMessageDto.builder()
                .id("test-id")
                .content("Test message content")
                .senderId("sender-123")
                .senderName("Test User")
                .recipientId("recipient-456")
                .type(ChatMessageDto.MessageType.CHAT)
                .timestamp(Instant.now())
                .build();

        savedEntity = ChatMessageEntity.builder()
                .id("test-id")
                .content("Test message content")
                .senderId("sender-123")
                .senderName("Test User")
                .recipientId("recipient-456")
                .messageType("CHAT")
                .createdAt(Instant.now())
                .build();
    }

    @Test
    @DisplayName("Should save message successfully")
    void saveMessage_ShouldSaveSuccessfully() {
        // Given - create entity that service will create
        ChatMessageEntity entityToSave = ChatMessageEntity.builder()
                .content("Test message content")
                .senderId("sender-123")
                .senderName("Test User")
                .recipientId("recipient-456")
                .messageType("CHAT")
                .build();
        
        given(chatMessageRepository.save(entityToSave)).willReturn(savedEntity);

        // When
        chatMessageService.saveMessage(testMessage);

        // Then - verify save was called
        verify(chatMessageRepository).save(entityToSave);
    }

    @Test
    @DisplayName("Should get conversation between two users")
    void getConversation_ShouldReturnMessages() {
        // Given
        List<ChatMessageEntity> entities = List.of(savedEntity);
        given(chatMessageRepository.findBySenderIdAndRecipientIdOrderByCreatedAtAsc("sender-123", "recipient-456"))
                .willReturn(entities);

        // When
        List<ChatMessageDto> result = chatMessageService.getConversation("sender-123", "recipient-456");

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).content()).isEqualTo("Test message content");
        assertThat(result.get(0).senderId()).isEqualTo("sender-123");
        assertThat(result.get(0).recipientId()).isEqualTo("recipient-456");
    }

    @Test
    @DisplayName("Should get all messages for a user")
    void getMessagesForUser_ShouldReturnMessages() {
        // Given
        List<ChatMessageEntity> entities = List.of(savedEntity);
        given(chatMessageRepository.findByRecipientIdOrderByCreatedAtAsc("recipient-456"))
                .willReturn(entities);

        // When
        List<ChatMessageDto> result = chatMessageService.getMessagesForUser("recipient-456");

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).recipientId()).isEqualTo("recipient-456");
    }

    @Test
    @DisplayName("Should return empty list when no messages found")
    void getConversation_ShouldReturnEmpty_WhenNoMessages() {
        // Given
        given(chatMessageRepository.findBySenderIdAndRecipientIdOrderByCreatedAtAsc("user1", "user2"))
                .willReturn(List.of());

        // When
        List<ChatMessageDto> result = chatMessageService.getConversation("user1", "user2");

        // Then
        assertThat(result).isEmpty();
    }
}
