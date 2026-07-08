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
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public Page<Category> getAllCategories(int page, int size, String sortBy, String sortDirection) {
        Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return categoryRepository.findAllActive(pageable);
    }

    public Category getCategoryById(String id) {
        return categoryRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new DataNotFoundException("Category not found"));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public Category addCategory(AddCategoryRequestDTO request) {
        Optional<Category> checkCategoryName = categoryRepository.findByNameAndDeletedAtIsNull(request.getName());

        if (checkCategoryName.isPresent()) {
            throw new BusinessException("Category already exists");
        }

        Category category = new Category();
        category.setName(request.getName());

        return categoryRepository.save(category);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public Category updateCategory(String id, AddCategoryRequestDTO request) {
        Category category = categoryRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new DataNotFoundException("Category not found"));

        Optional<Category> checkCategoryName = categoryRepository.findByNameAndDeletedAtIsNull(request.getName());

        if (checkCategoryName.isPresent()) {
            throw new BusinessException("Category already exists");
        }

        category.setName(request.getName());
        return categoryRepository.save(category);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public Category deleteCategory(String id) {
        Category category = categoryRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new DataNotFoundException("Category not found"));

        category.setDeletedAt(LocalDateTime.now());
        category.setDeletedBy(SecurityUtil.getCurrentUsername());
        return categoryRepository.save(category);
    }
}
