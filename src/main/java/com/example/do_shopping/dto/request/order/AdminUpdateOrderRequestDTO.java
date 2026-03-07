package com.example.do_shopping.dto.request.order;

import com.example.do_shopping.enums.OrderStatus;
import com.example.do_shopping.enums.ShippingStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class AdminUpdateOrderRequestDTO {
    private OrderStatus orderStatus;
    private String courierName;
    private String serviceType;
    private String trackingNumber;
    private BigDecimal shippingCost;
    private ShippingStatus shippingStatus;
    private LocalDateTime shippedAt;
    private LocalDateTime deliveredAt;
}
