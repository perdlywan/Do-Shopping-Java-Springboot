package com.example.do_shopping.dto.request.order;

import com.example.do_shopping.enums.PaymentMethod;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class AddOrderRequestDTO {
    @NotEmpty(message = "items tidak boleh kosong")
    @Valid
    private List<OrderItemRequestDTO> items;

    private String note;

    @NotNull(message = "paymentMethod tidak boleh kosong")
    private PaymentMethod paymentMethod;

    private String providerName;

    @NotNull(message = "shippingAddressId tidak boleh kosong")
    private String shippingAddressId;
}
