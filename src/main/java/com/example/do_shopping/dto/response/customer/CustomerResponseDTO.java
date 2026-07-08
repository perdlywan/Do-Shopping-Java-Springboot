package com.example.do_shopping.dto.response.customer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerResponseDTO {
    private String id;
    private String username;
    private String name;
    private String email;
    private Long totalOrders;
    private java.math.BigDecimal totalSpent;
    private String status;
}
