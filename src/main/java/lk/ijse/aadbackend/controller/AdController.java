package lk.ijse.aadbackend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.persistence.EntityNotFoundException;
import lk.ijse.aadbackend.dto.AdDTO;
import lk.ijse.aadbackend.dto.ResponseDTO;
import lk.ijse.aadbackend.entity.Ad;
import lk.ijse.aadbackend.repo.AdRepository;
import lk.ijse.aadbackend.service.AdService;
import lk.ijse.aadbackend.util.VarList;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

@CrossOrigin(origins = "http://localhost:63342") // Allow frontend URL
@RestController
@RequestMapping("api/v1/ad")
public class AdController {

    private final AdService adService;
    private final AdRepository adRepository;

    public AdController(AdService adService, AdRepository adRepository) {
        this.adService = adService;
        this.adRepository = adRepository;
    }


    @GetMapping("/getAllActiveAds")
    public ResponseEntity<ResponseDTO> getAllActiveAds() {
        List<AdDTO> activeAds = adService.getAllActiveAds();
//        System.out.println(activeAds);

        // Add Cache-Control headers to prevent caching
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0");

        return ResponseEntity.ok()
                .headers(headers)
                .body(new ResponseDTO(VarList.OK, "Active ads retrieved successfully", activeAds));
    }

    @GetMapping("/getAdDetailsByAdId/{adId}")
    public ResponseEntity<?> getAdById(@PathVariable UUID adId) {
        AdDTO ad = adService.getAdDetailsByAdId(adId);
        if (ad != null) {
            return ResponseEntity.ok(ad);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Ad not found");
        }
    }


    @PutMapping(value = "/updateAd", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<ResponseDTO> updateAd(
            @RequestParam("adDTO") String adDTOJson,
            @RequestPart(value = "images", required = false) List<MultipartFile> newImages) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            AdDTO adDTO = mapper.readValue(adDTOJson, AdDTO.class);

            int result = adService.createAd(adDTO, newImages); // implement in service

            if (result == VarList.Created) {
                return ResponseEntity.ok(new ResponseDTO(VarList.Created, "Ad updated", null));
            } else {
                return ResponseEntity.badRequest().body(new ResponseDTO(VarList.Bad_Gateway, "Update failed", null));
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(new ResponseDTO(VarList.Internal_Server_Error, e.getMessage(), null));
        }
    }



    @PostMapping(value = "/createAd", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<ResponseDTO> createAd(
            @RequestParam("adDTO") String adDTOJson,  // JSON as a String
            @RequestPart(value = "images", required = false) List<MultipartFile> imageFiles) {

        System.out.println("AdDTO JSON: " + adDTOJson);
        System.out.println("Total images received: " + (imageFiles != null ? imageFiles.size() : 0));

        for (MultipartFile file : imageFiles) {
            System.out.println("Received file: " + file.getOriginalFilename());
        }



        try {
            // Convert JSON String to AdDTO object
            ObjectMapper objectMapper = new ObjectMapper();
            AdDTO adDTO = objectMapper.readValue(adDTOJson, AdDTO.class);

            // Call service to create ad
            int createdAd = adService.createAd(adDTO, imageFiles);

            if (createdAd == VarList.Created) {
                return ResponseEntity.status(HttpStatus.CREATED)
                        .body(new ResponseDTO(VarList.Created, "Ad successfully created", adDTO));
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseDTO(VarList.Bad_Gateway, "Error while creating ad", null));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDTO(VarList.Internal_Server_Error, e.getMessage(), null));
        }
    }


    @PutMapping("/deleteAd/{adId}")
    public ResponseEntity<ResponseDTO> deleteAd(@PathVariable UUID adId) {
        int result = adService.deleteAd(adId);

        if (result == VarList.Created) {
            return ResponseEntity.ok(new ResponseDTO(VarList.Created, "Ad deleted successfully!", adId));
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDTO(VarList.Internal_Server_Error, "Error deleting ad", null));
        }
    }


    @GetMapping("/user/{userId}")
    public ResponseEntity<List<AdDTO>> getAdsByUserId(@PathVariable UUID userId) {
        List<AdDTO> ads = adService.getAdsByUserId(userId);
        return ResponseEntity.ok(ads);
    }

    @GetMapping("/get-ad-images/{adId}")
    public ResponseEntity<List<Map<String, String>>> getAdImages(@PathVariable UUID adId) throws IOException {
        // Retrieve the ad object from the database by adId
        Ad ad = adRepository.findById(adId).orElseThrow(() -> new EntityNotFoundException("Ad not found"));

        // Assuming the imageUrls column contains a comma-separated list of image file names
        String[] imageUrls = ad.getImages().split(",");

        List<Map<String, String>> images = new ArrayList<>();

        // For each image URL, load the corresponding image from the filesystem
        for (String imageUrl : imageUrls) {
            File file = new File("uploadImages/" + imageUrl.trim()); // Assuming image files are stored in the "uploadImages" folder
            if (file.exists()) {
                byte[] imageBytes = Files.readAllBytes(file.toPath());
                String base64 = "data:image/" + getExtension(file.getName()) + ";base64," +
                        Base64.getEncoder().encodeToString(imageBytes);

                Map<String, String> imageMap = new HashMap<>();
                imageMap.put("name", file.getName());
                imageMap.put("base64", base64);
                images.add(imageMap);
            }
        }

//        System.out.println(images);

        return ResponseEntity.ok(images);
    }

    // Helper method to get image extension
    private String getExtension(String fileName) {
        String ext = "";
        int i = fileName.lastIndexOf('.');
        if (i > 0) {
            ext = fileName.substring(i + 1).toLowerCase();
        }
        return ext;
    }

    @GetMapping("/count/by-parent-category/{parentCategoryId}")
    public ResponseEntity<Integer> getAdCountByParentCategory(@PathVariable UUID parentCategoryId) {
        return ResponseEntity.ok(adService.countActiveAdsByParentCategory(parentCategoryId));
    }


    @GetMapping("/filter")
    public ResponseEntity<List<AdDTO>> filterAds(
            @RequestParam(required = false) UUID subcategoryId,
            @RequestParam(required = false) UUID districtId,
            @RequestParam(required = false) UUID cityId
    ) {
        List<AdDTO> filteredAds = adService.filterAds(subcategoryId, districtId, cityId);
        return ResponseEntity.ok(filteredAds);
    }










}