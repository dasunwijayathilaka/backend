package lk.ijse.aadbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LocationDTO {
    private UUID id;
    private String name;
    private UUID parentLocationId;
    private String parentLocationName;
}
