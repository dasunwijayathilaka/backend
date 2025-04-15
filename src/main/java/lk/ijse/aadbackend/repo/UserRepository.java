package lk.ijse.aadbackend.repo;

import jakarta.validation.constraints.NotBlank;
import lk.ijse.aadbackend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;


public interface UserRepository extends JpaRepository<User, UUID> {

    User findByEmail(String userName);

    boolean existsByEmail(String email);

    int deleteByEmail(String userName);

    boolean existsByPhone(@NotBlank(message = "Phone number is required") String phone);


}