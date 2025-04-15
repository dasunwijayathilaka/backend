package lk.ijse.aadbackend.service.impl;

import lk.ijse.aadbackend.dto.UserDTO;
import lk.ijse.aadbackend.email.EmailService;
import lk.ijse.aadbackend.entity.User;
import lk.ijse.aadbackend.enums.Status;
import lk.ijse.aadbackend.repo.UserRepository;
import lk.ijse.aadbackend.service.UserService;
import lk.ijse.aadbackend.util.VarList;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;




@Service
@Transactional
public class UserServiceImpl implements UserDetailsService, UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private EmailService emailService;



    private static final String UPLOAD_DIR = "uploadImages/";


    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email);
        return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), getAuthority(user));
    }

    public UserDTO loadUserDetailsByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username);
        return modelMapper.map(user,UserDTO.class);
    }

    private Set<SimpleGrantedAuthority> getAuthority(User user) {
        Set<SimpleGrantedAuthority> authorities = new HashSet<>();
        authorities.add(new SimpleGrantedAuthority(user.getRole()));
        return authorities;
    }

    @Override
    public UserDTO searchUser(UserDTO userDTO) {
        if (userRepository.existsByEmail(userDTO.getEmail())) {
            User user = userRepository.findByEmail(userDTO.getEmail());
            return modelMapper.map(user, UserDTO.class);
        } else {
            return null;
        }
    }

    @Override
    public int saveUser(UserDTO userDTO, MultipartFile image) throws IOException {
        if (userRepository.existsByEmail(userDTO.getEmail())) {
            return VarList.Not_Acceptable;
        }
        if (userRepository.existsByPhone(userDTO.getPhone())) {
            return VarList.Not_Acceptable;
        }

        User user = modelMapper.map(userDTO, User.class);

        if (image != null && !image.isEmpty()) {
            if (!image.getContentType().startsWith("image/")) {
                throw new IllegalArgumentException("Only image files are allowed");
            }

            String imageName = saveImage(image);
            user.setUserImage(imageName); // Only filename or "uploadImages/" + imageName
        }

        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        user.setPassword(bCryptPasswordEncoder.encode(userDTO.getPassword()));

        userRepository.save(user);
        return VarList.Created;
    }

    private String saveImage(MultipartFile image) throws IOException {
        File uploadDir = new File(UPLOAD_DIR);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        String fileExtension = getFileExtension(image.getOriginalFilename());
        String uniqueFileName = UUID.randomUUID().toString() + fileExtension;
        String filePath = UPLOAD_DIR + uniqueFileName;

        Files.write(Paths.get(filePath), image.getBytes());
        return uniqueFileName;
    }

    private String getFileExtension(String filename) {
        return filename.lastIndexOf('.') > 0 ? filename.substring(filename.lastIndexOf('.')) : "";
    }


    @Override
    public UserDTO getUserById(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with ID: " + id));

        return mapUserToUserDTO(user);
    }

    private UserDTO mapUserToUserDTO(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setName(user.getName());
        userDTO.setEmail(user.getEmail());
        // Note: We typically don't send the password back
        userDTO.setPassword(null);
        userDTO.setPhone(user.getPhone());
        userDTO.setUserImage(user.getUserImage());
        userDTO.setIsEmailVerified(user.getIsEmailVerified());
        userDTO.setIsPhoneVerified(user.getIsPhoneVerified());
        userDTO.setRole(user.getRole());
        userDTO.setStatus(Status.valueOf(user.getStatus()));

        return userDTO;
    }





    @Override
    @Transactional
    public void updateUserPhoto(UUID id, String base64Image) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with ID: " + id));

        // Validate the base64 string (optional)
        if (base64Image == null || !base64Image.startsWith("data:image/")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid image format");
        }

        // Update user with the base64 image
        user.setUserImage(base64Image);
        user.setUpdatedAt(LocalDateTime.now());

        userRepository.save(user);
    }

    @Override
    @Transactional
    public void deleteUserPhoto(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with ID: " + id));

        // Remove the user's photo
        user.setUserImage(null);
        user.setUpdatedAt(LocalDateTime.now());

        userRepository.save(user);
    }



    @Override
    @Transactional
    public boolean changePassword(UUID userId, String currentPassword, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            return false;
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        return true;
    }




}
