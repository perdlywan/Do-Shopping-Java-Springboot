package com.example.do_shopping.dto.response.shipping;

import com.example.do_shopping.enums.ShippingStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class ShippingResponseDTO {
    private String id;
    private Object shippingAddress;
    private String courierName;
    private String serviceType;
    private String trackingNumber;
    private BigDecimal shippingCost;
    private ShippingStatus status;
    private LocalDateTime shippedAt;
    private LocalDateTime deliveredAt;
}
