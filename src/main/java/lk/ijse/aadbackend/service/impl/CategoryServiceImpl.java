package lk.ijse.aadbackend.service.impl;

import lk.ijse.aadbackend.dto.LocationDTO;
import lk.ijse.aadbackend.entity.Location;
import lk.ijse.aadbackend.service.CategoryService;

import lk.ijse.aadbackend.dto.CategoryDTO;
import lk.ijse.aadbackend.entity.Category;
import lk.ijse.aadbackend.repo.CategoryRepository;
import lk.ijse.aadbackend.service.CategoryService;
import lk.ijse.aadbackend.util.VarList;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ModelMapper modelMapper;

    private static final String UPLOAD_DIR = "uploadImages/";

    @Override
    public int createCategory(CategoryDTO categoryDTO, MultipartFile image) throws IOException {
        // Create a new Category entity manually
        Category category = new Category();

        // Manually set properties from DTO to entity
        category.setName(categoryDTO.getName());

        if (categoryDTO.getId() != null) {
            category.setId(categoryDTO.getId());
        }

        // Handle image if provided
        if (image != null && !image.isEmpty()) {
            String imageName = saveImage(image);
            category.setImageUrl(UPLOAD_DIR + imageName);
        } else if (categoryDTO.getImageUrl() != null) {
            // Use existing image URL if provided in the DTO
            category.setImageUrl(categoryDTO.getImageUrl());
        }

        // Handle parent category relationship
        if (categoryDTO.getParentCategoryId() != null) {
            Category parentCategory = categoryRepository.findById(categoryDTO.getParentCategoryId())
                    .orElseThrow(() -> new RuntimeException("Parent category not found"));
            category.setParentCategory(parentCategory);
        }

        // Save the category
        categoryRepository.save(category);

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
    public List<CategoryDTO> getAllCategories() {
        return categoryRepository.findAll()
                .stream()
                .map(category -> modelMapper.map(category, CategoryDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<CategoryDTO> getCategoryByParentCategoryId(UUID parentCategoryId) {
        List<Category> categories = categoryRepository.findByParentCategoryId(parentCategoryId);
        return categories.stream()
                .map(category -> modelMapper.map(category, CategoryDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<CategoryDTO> getCategoryByCategoryId(UUID id) {
        Optional<Category> categories = categoryRepository.findById(id);
        return categories.stream()
                .map(category -> modelMapper.map(category, CategoryDTO.class))
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public void deleteCategory(UUID id) {
        Optional<Category> optionalCategory = categoryRepository.findById(id);

        if (optionalCategory.isEmpty()) {
            throw new RuntimeException("Category not found");
        }

        Category category = optionalCategory.get();
        String imageUrl = category.getImageUrl(); // Store image URL before deletion

        // Try to delete category first
        categoryRepository.deleteById(id);
        System.out.println("Category deleted successfully: " + id);

        // If category deletion is successful, delete the image
        if (imageUrl != null && !imageUrl.isEmpty()) {
            try {
                String filename = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
                Path imagePath = Paths.get("uploadImages", filename);

                if (Files.exists(imagePath)) {
                    Files.delete(imagePath);
                    System.out.println("Image deleted successfully: " + filename);
                } else {
                    System.out.println("Image file not found: " + filename);
                }
            } catch (Exception e) {
                System.err.println("Failed to delete category image: " + e.getMessage());
            }
        }
    }


}

