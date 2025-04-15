package lk.ijse.aadbackend.controller;

import lk.ijse.aadbackend.dto.ChatDTO;
import lk.ijse.aadbackend.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/chat")
@CrossOrigin(origins = "http://localhost:63342")
public class ChatController {

    @Autowired
    private ChatService chatService;

    @PostMapping("/save")
    public ResponseEntity<ChatDTO> saveChat(@RequestBody ChatDTO chatDTO) {
        return ResponseEntity.ok(chatService.saveChat(chatDTO));
    }

    @GetMapping("/{user1Id}/{user2Id}/{adId}")
    public ResponseEntity<List<ChatDTO>> getConversation(
            @PathVariable UUID user1Id,
            @PathVariable UUID user2Id,
            @PathVariable UUID adId) {
        return ResponseEntity.ok(chatService.getConversation(user1Id, user2Id, adId));
    }

    @GetMapping("/ads/{userId}")
    public ResponseEntity<List<UUID>> getAdIdsWithChats(@PathVariable UUID userId) {
        return ResponseEntity.ok(chatService.getAdIdsWithChats(userId));
    }

    @PutMapping("/read/{chatId}")
    public ResponseEntity<Void> markAsRead(@PathVariable UUID chatId) {
        chatService.markAsRead(chatId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/unread/{receiverId}")
    public ResponseEntity<List<ChatDTO>> getUnreadMessages(@PathVariable UUID receiverId) {
        return ResponseEntity.ok(chatService.getUnreadMessages(receiverId));
    }

    @GetMapping("/{userId}/{adId}/participants")
    public ResponseEntity<List<UUID>> getConversationParticipants(
            @PathVariable UUID userId,
            @PathVariable UUID adId) {
        return ResponseEntity.ok(chatService.getConversationParticipants(userId, adId));
    }
}