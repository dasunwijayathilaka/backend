package lk.ijse.aadbackend.controller;

import jakarta.validation.Valid;
import lk.ijse.aadbackend.dto.AuthDTO;
import lk.ijse.aadbackend.dto.ChangePasswordRequestDTO;
import lk.ijse.aadbackend.dto.ResponseDTO;
import lk.ijse.aadbackend.dto.UserDTO;
import lk.ijse.aadbackend.service.UserService;
import lk.ijse.aadbackend.util.JwtUtil;
import lk.ijse.aadbackend.util.VarList;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@CrossOrigin(origins = "http://localhost:63342") // Allow frontend URL
@RestController
@RequestMapping("api/v1/user")
public class UserController {
    private final UserService userService;
    private final JwtUtil jwtUtil;

    //constructor injection
    public UserController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping(value = "/register", consumes = "multipart/form-data")
    public ResponseEntity<ResponseDTO> registerUser(
            @RequestPart @Valid UserDTO userDTO,
            @RequestPart(value = "image", required = false) MultipartFile image) {

        try {

            int res = userService.saveUser(userDTO, image);


            System.out.println("res: " + res);

            switch (res) {
                case VarList.Created -> {

                    System.out.println("In the switch case: " + userDTO);

                    String token = jwtUtil.generateToken(userDTO);
                    AuthDTO authDTO = new AuthDTO();
                    authDTO.setEmail(userDTO.getEmail());
                    authDTO.setToken(token);
                    return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseDTO(VarList.Created, "Success", authDTO));
                }
                case VarList.Not_Acceptable -> {
                    return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
                            .body(new ResponseDTO(VarList.Not_Acceptable, "Email or Phone Number is Already Used", null));
                }
                default -> {
                    return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                            .body(new ResponseDTO(VarList.Bad_Gateway, "Error", null));
                }
            }
        } catch (Exception e) {
            System.out.println("In the catch case: " + userDTO);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDTO(VarList.Internal_Server_Error, e.getMessage(), null));
        }
    }


    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable UUID id) {
        UserDTO user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }


    // Upload photo endpoint
    @PutMapping("/{id}/photo")
    public ResponseEntity<?> updateUserPhoto(@PathVariable UUID id, @RequestBody UserDTO photoDTO) {
        userService.updateUserPhoto(id, photoDTO.getUserImage());
        return ResponseEntity.ok().body(Collections.singletonMap("message", "Photo updated successfully"));
    }

    // Delete photo endpoint
    @DeleteMapping("/{id}/photo")
    public ResponseEntity<?> deleteUserPhoto(@PathVariable UUID id) {
        userService.deleteUserPhoto(id);
        return ResponseEntity.ok().body(Collections.singletonMap("message", "Photo deleted successfully"));
    }



    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@Valid @RequestBody ChangePasswordRequestDTO request) {
        boolean isChanged = userService.changePassword(request.getUserId(), request.getCurrentPassword(), request.getNewPassword());

        if (isChanged) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Password changed successfully");
            return ResponseEntity.ok(response);
        } else {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Current password is incorrect");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }


}

