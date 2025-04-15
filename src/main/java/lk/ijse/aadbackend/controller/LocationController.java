package lk.ijse.aadbackend.controller;

import lk.ijse.aadbackend.dto.LocationDTO;
import lk.ijse.aadbackend.service.LocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@CrossOrigin(origins = "http://localhost:63342") // Allow frontend URL
@RestController
@RequestMapping("/api/v1/location")
public class LocationController {

    @Autowired
    private LocationService locationService;

    @GetMapping("/parent/{parentLocationId}")
    public ResponseEntity<List<LocationDTO>> getLocationsByParentId(@PathVariable UUID parentLocationId) {
        return ResponseEntity.ok(locationService.getLocationsByParentId(parentLocationId));
    }

    @GetMapping("allSubCategories/exclude/{parentId}")
    public ResponseEntity<List<LocationDTO>> getLocations(@PathVariable UUID parentId) {
        List<LocationDTO> locations = locationService.getLocationsExcludingParent(parentId);
        return ResponseEntity.ok(locations);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LocationDTO> getLocationById(@PathVariable UUID id) {
        LocationDTO location = locationService.findById(id);
        return ResponseEntity.ok(location);
    }

}
