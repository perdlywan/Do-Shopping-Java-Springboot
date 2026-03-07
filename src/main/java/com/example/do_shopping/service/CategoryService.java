package com.example.do_shopping.service;

import com.example.do_shopping.config.SecurityUtil;
import com.example.do_shopping.dto.request.category.AddCategoryRequestDTO;
import com.example.do_shopping.entity.Category;
import com.example.do_shopping.exception.custom.BusinessException;
import com.example.do_shopping.exception.custom.DataNotFoundException;
import com.example.do_shopping.repository.CategoryRepository;
import jakarta.transaction.Transactional;
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

    public Category getCategoryById(Long id){
        return categoryRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new DataNotFoundException("Kategori tidak ditemukan"));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public Category addCategory(AddCategoryRequestDTO request){
        Optional<Category> checkCategoryName = categoryRepository.findByNameAndDeletedAtIsNull(request.getName());

        if(checkCategoryName.isPresent()){
            throw new BusinessException("Kategori sudah ada");
        }

        Category category = new Category();
        category.setName(request.getName());

        return categoryRepository.save(category);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public Category updateCategory(Long id, AddCategoryRequestDTO request){
        Category category = categoryRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new DataNotFoundException("Kategori tidak ditemukan"));

        Optional<Category> checkCategoryName = categoryRepository.findByNameAndDeletedAtIsNull(request.getName());

        if(checkCategoryName.isPresent()){
            throw new BusinessException("Kategori sudah ada");
        }

        category.setName(request.getName());
        return categoryRepository.save(category);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public Category deleteCategory(Long id){
        Category category = categoryRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new DataNotFoundException("Kategori tidak ditemukan"));

        category.setDeletedAt(LocalDateTime.now());
        category.setDeletedBy(SecurityUtil.getCurrentUsername());
        return categoryRepository.save(category);
    }
}
