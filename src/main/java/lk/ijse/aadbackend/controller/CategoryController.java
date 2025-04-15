package lk.ijse.aadbackend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lk.ijse.aadbackend.dto.CategoryDTO;
import lk.ijse.aadbackend.dto.ResponseDTO;
import lk.ijse.aadbackend.entity.Category;
import lk.ijse.aadbackend.service.AdService;
import lk.ijse.aadbackend.service.CategoryService;
import lk.ijse.aadbackend.service.impl.AdServiceImpl;
import lk.ijse.aadbackend.util.VarList;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@CrossOrigin
@RestController
@RequestMapping("/api/v1/category")
public class CategoryController {

    private final CategoryService categoryService;
    private final ObjectMapper objectMapper; // JSON parser
    private final ModelMapper modelMapper;
    private final AdServiceImpl adServiceImpl;

    public CategoryController(CategoryService categoryService, ObjectMapper objectMapper, ModelMapper modelMapper, AdServiceImpl adServiceImpl) {
        this.categoryService = categoryService;
        this.objectMapper = objectMapper;
        this.modelMapper = modelMapper;
        this.adServiceImpl = adServiceImpl;
    }

    @PostMapping(value = "/create", consumes = "multipart/form-data")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ResponseDTO> createCategory(
            @RequestPart("category") String categoryJson,
            @RequestPart(value = "image", required = false) MultipartFile image) {

        try {
            // Convert JSON string to CategoryDTO
            CategoryDTO categoryDTO = objectMapper.readValue(categoryJson, CategoryDTO.class);

            System.out.println("CategoryDTO: " + categoryDTO);

            int createdCategory = categoryService.createCategory(categoryDTO, image);

            if (createdCategory == VarList.Created) {
                return ResponseEntity.status(HttpStatus.CREATED)
                        .body(new ResponseDTO(VarList.Created, "Category successfully created", null));
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseDTO(VarList.Bad_Gateway, "Error while creating category", null));

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDTO(VarList.Internal_Server_Error, e.getMessage(), null));
        }
    }

    // fetch all categories with parent categories
    @GetMapping("/getAll")
    public ResponseEntity<List<CategoryDTO>> getAllCategories() {
        return ResponseEntity.ok(categoryService.getAllCategories());
    }


    @GetMapping("/{id}")
    public ResponseEntity<List<CategoryDTO>> getCategoryByParentCategoryId(@PathVariable UUID id) {
        return ResponseEntity.ok(categoryService.getCategoryByParentCategoryId(id));
    }

    @DeleteMapping("delete/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<String> deleteCategory(@PathVariable UUID id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.ok("Category deleted successfully");
    }

    @GetMapping("/cid/{id}")
    public ResponseEntity<List<CategoryDTO>> getCategoryByCategoryId(@PathVariable UUID id) {
        return ResponseEntity.ok(categoryService.getCategoryByCategoryId(id));
    }





}
