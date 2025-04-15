package lk.ijse.aadbackend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.UUID;

@Data
public class CategoryDTO {
    private UUID id;

    private String imageUrl;

    @NotBlank(message = "Category name is required")
    private String name;

    private UUID parentCategoryId;

    private String parentCategoryName;
}
