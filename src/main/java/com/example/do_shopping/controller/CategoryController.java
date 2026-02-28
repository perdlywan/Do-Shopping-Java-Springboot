package com.example.do_shopping.controller;

import com.example.do_shopping.dto.request.AddCategoryRequestDTO;
import com.example.do_shopping.entity.Category;
import com.example.do_shopping.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    @GetMapping
    public List<Category> getAllCatgories(){
        return categoryService.getAllCategories();
    }

    @PostMapping
    public void addPosition(@Valid @RequestBody AddCategoryRequestDTO request){
        categoryService.addCategory(request);
    }

    @PutMapping("/{id}")
    public void updateCategoryName(@PathVariable("id") Long id, @Valid @RequestBody AddCategoryRequestDTO request){
        categoryService.updateCategory(id, request);
    }

    @DeleteMapping("/{id}")
    public void deleteCategory(@PathVariable("id") Long id){
        categoryService.deleteCategory(id);
    }

}
