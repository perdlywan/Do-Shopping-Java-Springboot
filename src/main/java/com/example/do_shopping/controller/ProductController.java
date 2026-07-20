package com.example.do_shopping.controller;


import com.example.do_shopping.dto.request.product.AddProductRequestDTO;
import com.example.do_shopping.dto.request.product.UpdateProductRequestDTO;
import com.example.do_shopping.dto.response.ActionSuccessResponseDTO;
import com.example.do_shopping.dto.response.DataResponseDTO;
import com.example.do_shopping.dto.response.PagedResponseDTO;
import com.example.do_shopping.dto.response.category.CategoryResponseDTO;
import com.example.do_shopping.dto.response.product.ProductResponseDTO;
import org.springframework.data.domain.Page;
import com.example.do_shopping.entity.Product;
import com.example.do_shopping.service.ProductService;
import com.example.do_shopping.service.StorageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;
    private final StorageService storageService;

    @GetMapping
    public ResponseEntity<PagedResponseDTO<ProductResponseDTO>> getAllProducts(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection,
            @RequestParam(required = false) String categoryId
    ){
        int pageIndex = page > 0 ? page - 1 : 0;
        Page<Product> productsPage = productService.getAllProducts(pageIndex, size, sortBy, sortDirection, categoryId);

        List<ProductResponseDTO> productResponseDTO = productsPage.getContent().stream()
                .map(product -> new ProductResponseDTO (
                        product.getId(),
                        product.getCategory().getId(),
                        product.getName(),
                        product.getPrice(),
                        product.getStock(),
                        product.getDescription(),
                        product.getImageUrl()
                ))
                .collect(Collectors.toList());

        PagedResponseDTO<ProductResponseDTO> response = new PagedResponseDTO<>(
                HttpStatus.OK.value(),
                productResponseDTO,
                productsPage.getNumber() + 1,
                productsPage.getSize(),
                productsPage.getTotalElements(),
                productsPage.getTotalPages(),
                productsPage.isLast()
        );

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DataResponseDTO> getProductById(@PathVariable("id") String id){
        Product product =  productService.getProductById(id);

        ProductResponseDTO productResponseDTO = new ProductResponseDTO(
                product.getId(),
                product.getCategory().getId(),
                product.getName(),
                product.getPrice(),
                product.getStock(),
                product.getDescription(),
                product.getImageUrl()
        );

        DataResponseDTO response = new DataResponseDTO(
                HttpStatus.OK.value(),
                productResponseDTO
        );

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ActionSuccessResponseDTO> addProduct(
            @Valid @RequestPart("data") AddProductRequestDTO request,
            @RequestPart(value = "file", required = false) MultipartFile file) {
        
        String imageUrl = null;
        if (file != null && !file.isEmpty()) {
            imageUrl = storageService.saveProductImage(file);
        }

        Product product =  productService.addProduct(request, imageUrl);

        ProductResponseDTO productResponseDTO = new ProductResponseDTO(
                product.getId(),
                product.getCategory().getId(),
                product.getName(),
                product.getPrice(),
                product.getStock(),
                product.getDescription(),
                product.getImageUrl()
        );

        ActionSuccessResponseDTO response = new ActionSuccessResponseDTO(
                HttpStatus.CREATED.value(),
                "Product successfully added",
                productResponseDTO
        );

        return  ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ActionSuccessResponseDTO> updateProduct(
            @PathVariable("id") String id, 
            @Valid @RequestPart("data") UpdateProductRequestDTO request,
            @RequestPart(value = "file", required = false) MultipartFile file){
        
        String imageUrl = null;
        if (file != null && !file.isEmpty()) {
            Product existingProduct = productService.getProductById(id);
            if (existingProduct.getImageUrl() != null) {
                storageService.deleteFile(existingProduct.getImageUrl());
            }
            imageUrl = storageService.saveProductImage(file);
        }

        Product product =  productService.updateProduct(id, request, imageUrl);

        ProductResponseDTO productResponseDTO = new ProductResponseDTO(
                product.getId(),
                product.getCategory().getId(),
                product.getName(),
                product.getPrice(),
                product.getStock(),
                product.getDescription(),
                product.getImageUrl()
        );

        ActionSuccessResponseDTO response = new ActionSuccessResponseDTO(
                HttpStatus.OK.value(),
                "Product successfully updated",
                productResponseDTO
        );

        return  ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ActionSuccessResponseDTO> deleteProduct(@PathVariable("id") String id){
        Product product =  productService.deleteProduct(id);

        ProductResponseDTO productResponseDTO = new ProductResponseDTO(
                product.getId(),
                product.getCategory().getId(),
                product.getName(),
                product.getPrice(),
                product.getStock(),
                product.getDescription(),
                product.getImageUrl()
        );

        ActionSuccessResponseDTO response = new ActionSuccessResponseDTO(
                HttpStatus.OK.value(),
                "Product successfully deleted",
                productResponseDTO
        );

        return  ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping(value = "/{id}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ActionSuccessResponseDTO> uploadProductImage(
            @PathVariable("id") String id,
            @RequestParam("file") MultipartFile file) {

        Product product = productService.getProductById(id);

        // Delete old image if exists
        if (product.getImageUrl() != null) {
            storageService.deleteFile(product.getImageUrl());
        }

        String imageUrl = storageService.saveProductImage(file);
        product = productService.updateProductImage(id, imageUrl);

        ProductResponseDTO productResponseDTO = new ProductResponseDTO(
                product.getId(),
                product.getCategory().getId(),
                product.getName(),
                product.getPrice(),
                product.getStock(),
                product.getDescription(),
                product.getImageUrl()
        );

        ActionSuccessResponseDTO response = new ActionSuccessResponseDTO(
                HttpStatus.OK.value(),
                "Product image uploaded successfully",
                productResponseDTO
        );

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/{id}/image")
    public ResponseEntity<ActionSuccessResponseDTO> deleteProductImage(@PathVariable("id") String id) {
        Product product = productService.getProductById(id);

        if (product.getImageUrl() != null) {
            storageService.deleteFile(product.getImageUrl());
            product = productService.updateProductImage(id, null);
        }

        ActionSuccessResponseDTO response = new ActionSuccessResponseDTO(
                HttpStatus.OK.value(),
                "Product image deleted successfully",
                null
        );

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
