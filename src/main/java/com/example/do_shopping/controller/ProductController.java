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
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @GetMapping
    public ResponseEntity<PagedResponseDTO<ProductResponseDTO>> getAllProducts(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection
    ){
        int pageIndex = page > 0 ? page - 1 : 0;
        Page<Product> productsPage = productService.getAllProducts(pageIndex, size, sortBy, sortDirection);

        List<ProductResponseDTO> productResponseDTO = productsPage.getContent().stream()
                .map(product -> new ProductResponseDTO (
                        product.getId(),
                        product.getCategory().getId(),
                        product.getName(),
                        product.getPrice(),
                        product.getStock(),
                        product.getDescription()
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
                product.getDescription()
        );

        DataResponseDTO response = new DataResponseDTO(
                HttpStatus.OK.value(),
                productResponseDTO
        );

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping
    public ResponseEntity<ActionSuccessResponseDTO> addProduct(@Valid @RequestBody AddProductRequestDTO request){
        Product product =  productService.addProduct(request);

        ProductResponseDTO productResponseDTO = new ProductResponseDTO(
                product.getId(),
                product.getCategory().getId(),
                product.getName(),
                product.getPrice(),
                product.getStock(),
                product.getDescription()
        );

        ActionSuccessResponseDTO response = new ActionSuccessResponseDTO(
                HttpStatus.CREATED.value(),
                "Produk berhasil ditambahkan",
                productResponseDTO
        );

        return  ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ActionSuccessResponseDTO> updateProduct(@PathVariable("id") String id, @Valid @RequestBody UpdateProductRequestDTO request){
        Product product =  productService.updateProduct(id, request);

        ProductResponseDTO productResponseDTO = new ProductResponseDTO(
                product.getId(),
                product.getCategory().getId(),
                product.getName(),
                product.getPrice(),
                product.getStock(),
                product.getDescription()
        );

        ActionSuccessResponseDTO response = new ActionSuccessResponseDTO(
                HttpStatus.OK.value(),
                "Produk berhasil diupdate",
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
                product.getDescription()
        );

        ActionSuccessResponseDTO response = new ActionSuccessResponseDTO(
                HttpStatus.OK.value(),
                "Produk berhasil dihapus",
                productResponseDTO
        );

        return  ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
