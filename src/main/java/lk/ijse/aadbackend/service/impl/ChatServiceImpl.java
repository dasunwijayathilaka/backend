package lk.ijse.aadbackend.service.impl;

import lk.ijse.aadbackend.dto.ChatDTO;
import lk.ijse.aadbackend.entity.Ad;
import lk.ijse.aadbackend.entity.Chat;
import lk.ijse.aadbackend.entity.User;
import lk.ijse.aadbackend.repo.AdRepository;
import lk.ijse.aadbackend.repo.ChatRepository;
import lk.ijse.aadbackend.repo.UserRepository;
import lk.ijse.aadbackend.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ChatServiceImpl implements ChatService {

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AdRepository adRepository;

    @Override
    public ChatDTO saveChat(ChatDTO chatDTO) {
        Chat chat = new Chat();
        
        if (chatDTO.getId() != null) {
            chat.setId(chatDTO.getId());
        }
        
        chat.setMessage(chatDTO.getMessage());
        chat.setStatus(chatDTO.getStatus());
        
        if (chatDTO.getTimestamp() != null) {
            chat.setTimestamp(chatDTO.getTimestamp());
        } else {
            chat.setTimestamp(LocalDateTime.now());
        }
        
        User sender = userRepository.findById(chatDTO.getSenderId())
                .orElseThrow(() -> new RuntimeException("Sender not found"));
        User receiver = userRepository.findById(chatDTO.getReceiverId())
                .orElseThrow(() -> new RuntimeException("Receiver not found"));
        Ad ad = adRepository.findById(chatDTO.getAdId())
                .orElseThrow(() -> new RuntimeException("Ad not found"));
        
        chat.setSender(sender);
        chat.setReceiver(receiver);
        chat.setAd(ad);
        
        Chat savedChat = chatRepository.save(chat);
        return convertToDTO(savedChat);
    }

    @Override
    public List<ChatDTO> getConversation(UUID user1Id, UUID user2Id, UUID adId) {
        User user1 = userRepository.findById(user1Id)
                .orElseThrow(() -> new RuntimeException("User 1 not found"));
        User user2 = userRepository.findById(user2Id)
                .orElseThrow(() -> new RuntimeException("User 2 not found"));
        
        List<Chat> chats = chatRepository.findConversation(user1, user2, adId);
        return chats.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    public List<UUID> getAdIdsWithChats(UUID userId) {
        return chatRepository.findAdIdsWithChatsByUserId(userId);
    }

    @Override
    public void markAsRead(UUID chatId) {
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new RuntimeException("Chat not found"));
        chat.setStatus("READ");
        chatRepository.save(chat);
    }

    @Override
    public List<ChatDTO> getUnreadMessages(UUID receiverId) {
        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new RuntimeException("Receiver not found"));
        
        List<Chat> unreadMessages = chatRepository.findAll().stream()
                .filter(chat -> chat.getReceiver().equals(receiver) && "SENT".equals(chat.getStatus()))
                .collect(Collectors.toList());
        
        return unreadMessages.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    private ChatDTO convertToDTO(Chat chat) {
        return new ChatDTO(
                chat.getId(),
                chat.getMessage(),
                chat.getStatus(),
                chat.getTimestamp(),
                chat.getSender().getId(),
                chat.getReceiver().getId(),
                chat.getAd().getId()
        );
    }

    @Override
    public List<UUID> getConversationParticipants(UUID userId, UUID adId) {
        // Find all chats for this ad where the user is either sender or receiver
        List<Chat> chats = chatRepository.findByAdIdAndUserInvolved(adId, userId);

        // Extract and return the IDs of the other users
        return chats.stream()
                .map(chat -> chat.getSender().getId().equals(userId) ?
                        chat.getReceiver().getId() : chat.getSender().getId())
                .distinct()
                .collect(Collectors.toList());
    }
}