package lk.ijse.aadbackend.service;

import lk.ijse.aadbackend.dto.AdDTO;
import lk.ijse.aadbackend.entity.Ad;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public interface AdService {
    int createAd(AdDTO adDTO, List<MultipartFile> imageFiles);

    int deleteAd(UUID adId);

    List<AdDTO> getAllActiveAds();

    List<AdDTO> getAdsByUserId(UUID userId);

    AdDTO getAdDetailsByAdId(UUID adId);

    int updateAd(AdDTO adDTO, List<MultipartFile> newImages) throws IOException;

    int countActiveAdsByParentCategory(UUID parentCategoryId);

    List<AdDTO> filterAds(UUID subcategoryId, UUID districtId, UUID cityId);

}