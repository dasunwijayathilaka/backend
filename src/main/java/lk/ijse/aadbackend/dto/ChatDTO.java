package lk.ijse.aadbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatDTO {
    private UUID id;
    private String message;
    private String status;
    private LocalDateTime timestamp;
    private UUID senderId;
    private UUID receiverId;
    private UUID adId;
}
