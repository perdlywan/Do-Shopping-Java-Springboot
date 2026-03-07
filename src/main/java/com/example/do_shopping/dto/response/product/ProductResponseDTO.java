package com.example.do_shopping.dto.response.product;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class ProductResponseDTO {
    private Long id;
    private Long category_id;
    private String name;
    private BigDecimal price;
    private Integer stock;
    private String description;
}
