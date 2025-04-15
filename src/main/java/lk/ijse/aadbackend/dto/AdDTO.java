package lk.ijse.aadbackend.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.List;
import java.util.UUID;


@Data
public class AdDTO {
    private UUID id;

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Description is required")
    private String description;

    @NotNull(message = "Price is required")
    @Positive(message = "Price must be greater than zero")
    private Double price;

    @NotBlank(message = "Status is required")
    private String status;

    @NotNull(message = "User ID is required")
    private UUID userId;

    @NotNull(message = "Category ID is required")
    private UUID categoryId;

    @NotNull(message = "Location ID is required")
    private UUID locationId;

    @NotNull(message = "User Name is required")
    private String userName;

    private String userPhone;

    private String userImage;

    private String userCreatedAt;

    private String categoryName;

    private String locationName;

    private String parentLocationName;

    private String createdAt;

//    // List of image files to be uploaded
//    private List<MultipartFile> imageFiles;

    private List<String> imageUrls;

    private List<String> existingImageNames;
}
