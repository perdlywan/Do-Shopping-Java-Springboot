package com.example.do_shopping.dto.response.dashboard;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.example.do_shopping.enums.OrderStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardRecentOrderDTO {
    String orderId;
    String orderNumber;
    String customerName;
    LocalDateTime orderDate;
    BigDecimal totalAmount;
    OrderStatus status;
}
