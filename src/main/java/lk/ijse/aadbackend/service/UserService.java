package lk.ijse.aadbackend.service;


import lk.ijse.aadbackend.dto.UserDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;


public interface UserService {
    int saveUser(UserDTO userDTO, MultipartFile image) throws IOException;
    UserDTO searchUser(UserDTO userDTO);
    UserDTO getUserById(UUID id);

    void updateUserPhoto(UUID id, String base64Image);

    void deleteUserPhoto(UUID id);

    boolean changePassword(UUID userId, String currentPassword, String newPassword);



}