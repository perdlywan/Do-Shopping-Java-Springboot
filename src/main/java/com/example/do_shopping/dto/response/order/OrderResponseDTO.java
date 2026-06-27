package com.example.do_shopping.dto.response.order;


import com.example.do_shopping.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class OrderResponseDTO {
    private String id;
    private String customerId;
    private LocalDateTime orderDate;
    private Integer totalQuantity;
    private BigDecimal totalAmount;
    private String note;
    private OrderStatus status;
    private Object orderDetail;
    private Object payment;
    private Object shipping;
}
