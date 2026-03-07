package com.example.do_shopping.dto.request.order;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OrderItemRequestDTO {
    @NotNull (message = "productId tidak boleh kosong")
    private Long productId;

    @NotNull(message = "quantity tidak boleh null")
    @Min(value = 1, message = "quantity minimal 1")
    private Integer quantity;
}
