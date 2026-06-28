package com.example.do_shopping.service;

import com.example.do_shopping.config.SecurityUtil;
import com.example.do_shopping.dto.request.product.AddProductRequestDTO;
import com.example.do_shopping.dto.request.product.UpdateProductRequestDTO;
import com.example.do_shopping.entity.Category;
import com.example.do_shopping.entity.Product;
import com.example.do_shopping.exception.custom.BusinessException;
import com.example.do_shopping.exception.custom.DataNotFoundException;
import com.example.do_shopping.repository.CategoryRepository;
import com.example.do_shopping.repository.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public Page<Product> getAllProducts(int page, int size, String sortBy, String sortDirection){
        Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return productRepository.findAllActive(pageable);
    }

    public Product getProductById(String id){
        return productRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new DataNotFoundException("Produk tidak ditemukan"));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public Product addProduct(AddProductRequestDTO request){
        Category category = categoryRepository.findByIdAndDeletedAtIsNull(request.getCategoryId())
                .orElseThrow(() -> new DataNotFoundException("category_id " + request.getCategoryId() + " tidak tersedia"));

        Optional<Product> checkProductName = productRepository.findByNameAndDeletedAtIsNull(request.getName());

        if(checkProductName.isPresent()){
            throw new BusinessException("Nama produk sudah ada");
        }

        Product product = new Product();
        product.setCategory(category);
        product.setName(request.getName());
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());
        product.setDescription(request.getDescription());
        return productRepository.save(product);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public Product updateProduct(String id, UpdateProductRequestDTO request){
        Product product = productRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new DataNotFoundException("product_id " + id + " tidak ditemukan"));

        if (request.getCategoryId() != null) {
            Category category = categoryRepository
                    .findByIdAndDeletedAtIsNull(request.getCategoryId())
                    .orElseThrow(() -> new DataNotFoundException(
                            "category_id " + request.getCategoryId() + " tidak tersedia"));
            product.setCategory(category);
        }

        Optional<Product> checkProductName = productRepository.findByNameAndDeletedAtIsNull(request.getName());

        if(checkProductName.isPresent() && !checkProductName.get().getId().equals(id)){
            throw new BusinessException("Nama Produk sudah ada");
        }

        if(request.getName() != null && !request.getName().isBlank()){
            product.setName(request.getName());
        }

        if(request.getPrice() != null && request.getPrice().compareTo(BigDecimal.ZERO) > 0){
            product.setPrice(request.getPrice());
        }

        if(request.getStock() != null){
            product.setStock(request.getStock());
        }

        if(request.getDescription() != null && !request.getDescription().isBlank()){
            product.setDescription(request.getDescription());
        }

        return productRepository.save(product);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public Product deleteProduct(String id){
        Product product = productRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new DataNotFoundException("product_id " + id + " tidak ditemukan"));

        product.setDeletedAt(LocalDateTime.now());
        product.setDeletedBy(SecurityUtil.getCurrentUsername());
        return productRepository.save(product);
    }
}
