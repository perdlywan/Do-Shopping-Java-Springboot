package com.example.do_shopping.dto.request.product;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class AddProductRequestDTO {
    @NotNull(message = "categoryId cannot be empty")
    private String categoryId;

    @NotBlank(message = "name cannot be empty")
    private String name;

    @NotNull(message = "price cannot be empty")
    @Positive(message = "price must be greater than 0")
    private BigDecimal price;

    @NotNull(message = "stock cannot be empty")
    private int stock;

    private String description;
}
