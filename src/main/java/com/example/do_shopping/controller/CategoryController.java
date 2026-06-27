package com.example.do_shopping.controller;

import com.example.do_shopping.dto.request.category.AddCategoryRequestDTO;
import com.example.do_shopping.dto.response.ActionSuccessResponseDTO;
import com.example.do_shopping.dto.response.DataResponseDTO;
import com.example.do_shopping.dto.response.category.CategoryResponseDTO;
import com.example.do_shopping.entity.Category;
import com.example.do_shopping.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<DataResponseDTO> getAllCatgories(){
        List<Category> categories = categoryService.getAllCategories();

        List<CategoryResponseDTO> categoryResponseDTO = categories.stream()
                .map(category -> new CategoryResponseDTO(
                        category.getId(),
                        category.getName()
                ))
                .collect(Collectors.toList());

        DataResponseDTO response = new DataResponseDTO(
                HttpStatus.OK.value(),
                categoryResponseDTO
        );

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DataResponseDTO> getCategoryById(@PathVariable("id") String id){
         Category category = categoryService.getCategoryById(id);

         CategoryResponseDTO categoryResponseDTO = new CategoryResponseDTO(category.getId(), category.getName());

         DataResponseDTO response = new DataResponseDTO(
                 HttpStatus.OK.value(),
                 categoryResponseDTO
         );

         return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping
    public ResponseEntity<ActionSuccessResponseDTO> addCategory(@Valid @RequestBody AddCategoryRequestDTO request){
        Category category =  categoryService.addCategory(request);

        CategoryResponseDTO categoryResponseDTO = new CategoryResponseDTO(category.getId(), category.getName());

        ActionSuccessResponseDTO response = new ActionSuccessResponseDTO(
                HttpStatus.CREATED.value(),
                "Kategori berhasil ditambahkan",
                categoryResponseDTO
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ActionSuccessResponseDTO> updateCategoryName(@PathVariable("id") String id, @Valid @RequestBody AddCategoryRequestDTO request){
        Category category =  categoryService.updateCategory(id, request);

        CategoryResponseDTO categoryResponseDTO = new CategoryResponseDTO(category.getId(), category.getName());

        ActionSuccessResponseDTO response = new ActionSuccessResponseDTO(
                HttpStatus.OK.value(),
                "Kategori berhasil diupdate",
                categoryResponseDTO
        );

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ActionSuccessResponseDTO> deleteCategory(@PathVariable("id") String id){
        Category category = categoryService.deleteCategory(id);

        CategoryResponseDTO categoryResponseDTO = new CategoryResponseDTO(category.getId(), category.getName());

        ActionSuccessResponseDTO response = new ActionSuccessResponseDTO(
                HttpStatus.OK.value(),
                "Kategori berhasil dihapus",
                categoryResponseDTO
        );

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}
