package com.example.do_shopping.dto.request.product;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class AddProductRequestDTO {
    @NotNull(message = "categoryId tidak boleh kosong")
    private Long categoryId;

    @NotBlank(message = "name tidak boleh kosong")
    private String name;

    @NotNull(message = "price tidak boleh kosong")
    @Positive(message = "price harus lebih besar dari 0")
    private BigDecimal price;

    @NotNull(message = "stock tidak boleh kosong")
    private int stock;

    private String description;
}
