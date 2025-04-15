package lk.ijse.aadbackend.repo;

import lk.ijse.aadbackend.entity.Chat;
import lk.ijse.aadbackend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ChatRepository extends JpaRepository<Chat, UUID> {
    
    List<Chat> findBySenderAndReceiverAndAdIdOrderByTimestampAsc(User sender, User receiver, UUID adId);
    
    @Query("SELECT c FROM Chat c WHERE (c.sender = :user1 AND c.receiver = :user2 AND c.ad.id = :adId) OR " +
           "(c.sender = :user2 AND c.receiver = :user1 AND c.ad.id = :adId) ORDER BY c.timestamp ASC")
    List<Chat> findConversation(User user1, User user2, UUID adId);
    
    @Query("SELECT DISTINCT c.ad.id FROM Chat c WHERE c.sender.id = :userId OR c.receiver.id = :userId")
    List<UUID> findAdIdsWithChatsByUserId(UUID userId);

    @Query("SELECT c FROM Chat c WHERE c.ad.id = :adId AND (c.sender.id = :userId OR c.receiver.id = :userId)")
    List<Chat> findByAdIdAndUserInvolved(UUID adId, UUID userId);
}