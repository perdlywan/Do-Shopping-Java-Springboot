package com.example.do_shopping.service;

import com.example.do_shopping.entity.*;
import com.example.do_shopping.enums.OrderStatus;
import com.example.do_shopping.enums.PaymentStatus;
import com.example.do_shopping.exception.custom.BusinessException;
import com.example.do_shopping.exception.custom.DataNotFoundException;
import com.example.do_shopping.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor

public class PaymentService {
    private final AuthService authService;
    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;

    @PreAuthorize("hasRole('CUSTOMER')")
    @Transactional
    public Payment doPayment(String id) {
        Customer customer = authService.getCurrentCustomer();

        Order order = orderRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new DataNotFoundException("Order not found"));

        if (!customer.getId().equals(order.getCustomer().getId())) {
            throw new AccessDeniedException("Order does not belong to you");
        }

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new BusinessException("Order cannot be paid");
        }

        Payment payment = paymentRepository.findByOrderIdAndPaidAtIsNull(id)
                .orElseThrow(() -> new DataNotFoundException("Order not found or already paid"));

        if (payment.getPaymentExpiredAt() != null && payment.getPaymentExpiredAt().isBefore(LocalDateTime.now())) {
            payment.setStatus(PaymentStatus.EXPIRED);
            paymentRepository.save(payment);
            throw new BusinessException("Payment time has expired");
        }

        order.setStatus(OrderStatus.PAID);
        orderRepository.save(order);

        payment.setPaidAt(LocalDateTime.now());
        payment.setStatus(PaymentStatus.PAID);
        return paymentRepository.save(payment);
    }

}
