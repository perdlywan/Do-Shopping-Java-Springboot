package com.example.do_shopping.dto.request.product;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class UpdateProductRequestDTO {
    private Long categoryId;
    private String name;
    private BigDecimal price;
    private Integer stock;
    private String description;
}
