package lk.ijse.aadbackend.dto;

import jakarta.persistence.Column;
import jakarta.persistence.Lob;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;


@AllArgsConstructor
@NoArgsConstructor
@Data
@Component
public class AuthDTO {
    private UUID id;
    private String email;
    private String token;
    private String role;
    private String name;

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String userImage;

}