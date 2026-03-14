package fr.matteofierquin.springchat.controller;

import fr.matteofierquin.springchat.dto.ChatMessageDto;
import fr.matteofierquin.springchat.service.ChatMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class ChatRestController {

    private final ChatMessageService chatMessageService;

    @GetMapping("/conversation/{otherUserId}")
    public ResponseEntity<List<ChatMessageDto>> getConversation(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable String otherUserId) {
        return ResponseEntity.ok(chatMessageService.getConversation(userDetails.getUsername(), otherUserId));
    }

    @GetMapping("/all")
    public ResponseEntity<List<ChatMessageDto>> getAllMessagesForUser(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(chatMessageService.getMessagesForUser(userDetails.getUsername()));
    }
}