package lk.ijse.aadbackend.repo;

import lk.ijse.aadbackend.entity.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface LocationRepository extends JpaRepository<Location, UUID> {
    List<Location> findByParentLocationId(UUID parentLocationId);
    List<Location> findByParentLocationIdNot(UUID parentId);
}
