package lk.ijse.aadbackend.service;

import lk.ijse.aadbackend.dto.LocationDTO;

import java.util.List;
import java.util.UUID;

public interface LocationService {

    List<LocationDTO> getLocationsByParentId(UUID parentLocationId);

    List<LocationDTO> getLocationsExcludingParent(UUID parentId);

    LocationDTO findById(UUID id);
}
