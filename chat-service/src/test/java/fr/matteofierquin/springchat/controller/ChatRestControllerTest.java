package fr.matteofierquin.springchat.controller;

import fr.matteofierquin.springchat.dto.ChatMessageDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ChatRestController Unit Tests")
class ChatRestControllerTest {

    @Test
    @DisplayName("Controller should load context")
    void controllerShouldLoad() {
        // Simple test to verify the controller can be instantiated
        ChatRestController controller = new ChatRestController(null);
        assertThat(controller).isNotNull();
    }

    @Test
    @DisplayName("ChatMessageDto should create correctly")
    void chatMessageDtoShouldCreateCorrectly() {
        // Given
        String content = "Test message";
        String senderId = "user1";
        String recipientId = "user2";

        // When
        ChatMessageDto message = ChatMessageDto.of(content, senderId, recipientId);

        // Then
        assertThat(message.content()).isEqualTo(content);
        assertThat(message.senderId()).isEqualTo(senderId);
        assertThat(message.recipientId()).isEqualTo(recipientId);
        assertThat(message.type()).isEqualTo(ChatMessageDto.MessageType.CHAT);
    }

    @Test
    @DisplayName("ChatMessageDto builder should work")
    void chatMessageDtoBuilderShouldWork() {
        // Given
        Instant now = Instant.now();

        // When
        ChatMessageDto message = ChatMessageDto.builder()
                .id("msg-1")
                .content("Hello")
                .senderId("user1")
                .senderName("User One")
                .recipientId("user2")
                .type(ChatMessageDto.MessageType.CHAT)
                .timestamp(now)
                .build();

        // Then
        assertThat(message.id()).isEqualTo("msg-1");
        assertThat(message.content()).isEqualTo("Hello");
        assertThat(message.senderId()).isEqualTo("user1");
        assertThat(message.recipientId()).isEqualTo("user2");
        assertThat(message.timestamp()).isEqualTo(now);
    }
}
