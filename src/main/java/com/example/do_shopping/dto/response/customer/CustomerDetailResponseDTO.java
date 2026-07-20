package com.example.do_shopping.dto.response.customer;

import com.example.do_shopping.dto.response.PagedResponseDTO;
import com.example.do_shopping.dto.response.order.OrderResponseDTO;
import com.example.do_shopping.dto.response.shippingAddress.ShippingAddressResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerDetailResponseDTO {
    private String id;
    private String username;
    private String name;
    private String email;
    private String phone;
    private Long totalOrders;
    private java.math.BigDecimal totalSpent;
    private String status;
    private LocalDateTime createdAt;
    private PagedResponseDTO<OrderResponseDTO> recentOrders;
    private List<ShippingAddressResponseDTO> addresses;
}
