package com.example.do_shopping.service;

import com.example.do_shopping.dto.request.AddCategoryRequestDTO;
import com.example.do_shopping.dto.response.CategoryResponseDTO;
import com.example.do_shopping.entity.Category;
import com.example.do_shopping.exception.custom.BusinessException;
import com.example.do_shopping.exception.custom.DataNotFoundException;
import com.example.do_shopping.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public List<Category> getAllCategories(){
            return categoryRepository.findAllActive();
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void addCategory(AddCategoryRequestDTO request){
        Optional<Category> checkCategoryName = categoryRepository.findByName(request.getName());

        if(checkCategoryName.isPresent()){
            throw new BusinessException("Kategori sudah ada");
        }

        Category category = new Category();
        category.setName(request.getName());
        categoryRepository.save(category);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void updateCategory(Long id, AddCategoryRequestDTO request){
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Kategori tidak ditemukan"));

        Optional<Category> checkCategoryName = categoryRepository.findByName(request.getName());

        if(checkCategoryName.isPresent()){
            throw new BusinessException("Kategori sudah ada");
        }

        category.setName(request.getName());
        categoryRepository.save(category);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void deleteCategory(Long id){
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Kategori tidak ditemukan"));

        category.setDeleted_at(LocalDateTime.now());
        categoryRepository.save(category);
    }
}
