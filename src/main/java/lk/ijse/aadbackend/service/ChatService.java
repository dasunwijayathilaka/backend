package lk.ijse.aadbackend.service;

import lk.ijse.aadbackend.dto.ChatDTO;
import lk.ijse.aadbackend.entity.Chat;

import java.util.List;
import java.util.UUID;

public interface ChatService {
    ChatDTO saveChat(ChatDTO chatDTO);
    List<ChatDTO> getConversation(UUID user1Id, UUID user2Id, UUID adId);
    List<UUID> getAdIdsWithChats(UUID userId);
    void markAsRead(UUID chatId);
    List<ChatDTO> getUnreadMessages(UUID receiverId);

    List<UUID> getConversationParticipants(UUID userId, UUID adId);
}