package com.example.do_shopping.dto.response.payment;

import com.example.do_shopping.enums.PaymentMethod;
import com.example.do_shopping.enums.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class PaymentResponseDTO {
    private String id;
    private PaymentMethod methodType;
    private String providerName;
    private BigDecimal amount;
    private LocalDateTime paymentExpiredAt;
    private PaymentStatus status;
    private LocalDateTime paidAt;
}
