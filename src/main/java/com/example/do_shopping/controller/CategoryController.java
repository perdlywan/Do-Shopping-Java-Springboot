package com.example.do_shopping.controller;

import com.example.do_shopping.dto.request.category.AddCategoryRequestDTO;
import com.example.do_shopping.dto.response.ActionSuccessResponseDTO;
import com.example.do_shopping.dto.response.DataResponseDTO;
import com.example.do_shopping.dto.response.PagedResponseDTO;
import com.example.do_shopping.dto.response.category.CategoryResponseDTO;
import org.springframework.data.domain.Page;
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
        public ResponseEntity<PagedResponseDTO<CategoryResponseDTO>> getAllCatgories(
                        @RequestParam(defaultValue = "1") int page,
                        @RequestParam(defaultValue = "10") int size,
                        @RequestParam(defaultValue = "id") String sortBy,
                        @RequestParam(defaultValue = "asc") String sortDirection) {
                int pageIndex = page > 0 ? page - 1 : 0;
                Page<Category> categoriesPage = categoryService.getAllCategories(pageIndex, size, sortBy,
                                sortDirection);

                List<CategoryResponseDTO> categoryResponseDTO = categoriesPage.getContent().stream()
                                .map(this::mapToResponseDTO)
                                .collect(Collectors.toList());

                PagedResponseDTO<CategoryResponseDTO> response = new PagedResponseDTO<>(
                                HttpStatus.OK.value(),
                                categoryResponseDTO,
                                categoriesPage.getNumber() + 1,
                                categoriesPage.getSize(),
                                categoriesPage.getTotalElements(),
                                categoriesPage.getTotalPages(),
                                categoriesPage.isLast());

                return ResponseEntity.status(HttpStatus.OK).body(response);
        }

        @GetMapping("/{id}")
        public ResponseEntity<DataResponseDTO> getCategoryById(@PathVariable("id") String id) {
                Category category = categoryService.getCategoryById(id);

                CategoryResponseDTO categoryResponseDTO = mapToResponseDTO(category);

                DataResponseDTO response = new DataResponseDTO(
                                HttpStatus.OK.value(),
                                categoryResponseDTO);

                return ResponseEntity.status(HttpStatus.OK).body(response);
        }

        @PostMapping
        public ResponseEntity<ActionSuccessResponseDTO> addCategory(@Valid @RequestBody AddCategoryRequestDTO request) {
                Category category = categoryService.addCategory(request);

                CategoryResponseDTO categoryResponseDTO = mapToResponseDTO(category);

                ActionSuccessResponseDTO response = new ActionSuccessResponseDTO(
                                HttpStatus.CREATED.value(),
                                "Category successfully added",
                                categoryResponseDTO);

                return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }

        @PutMapping("/{id}")
        public ResponseEntity<ActionSuccessResponseDTO> updateCategoryName(@PathVariable("id") String id,
                        @Valid @RequestBody AddCategoryRequestDTO request) {
                Category category = categoryService.updateCategory(id, request);

                CategoryResponseDTO categoryResponseDTO = mapToResponseDTO(category);

                ActionSuccessResponseDTO response = new ActionSuccessResponseDTO(
                                HttpStatus.OK.value(),
                                "Category successfully updated",
                                categoryResponseDTO);

                return ResponseEntity.status(HttpStatus.OK).body(response);
        }

        @DeleteMapping("/{id}")
        public ResponseEntity<ActionSuccessResponseDTO> deleteCategory(@PathVariable("id") String id) {
                Category category = categoryService.deleteCategory(id);

                CategoryResponseDTO categoryResponseDTO = mapToResponseDTO(category);

                ActionSuccessResponseDTO response = new ActionSuccessResponseDTO(
                                HttpStatus.OK.value(),
                                "Category successfully deleted",
                                categoryResponseDTO);

                return ResponseEntity.status(HttpStatus.OK).body(response);
        }

        private CategoryResponseDTO mapToResponseDTO(Category category) {
                return new CategoryResponseDTO(category.getId(), category.getName());
        }
}
