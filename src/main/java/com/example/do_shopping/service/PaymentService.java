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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor

public class PaymentService {
    private final AuthService authService;
    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final ProductRepository productRepository;
    private final OrderDetailRepository orderDetailRepository;

    @PreAuthorize("hasRole('CUSTOMER')")
    @Transactional
    public Payment doPayment(String id){
        Customer customer = authService.getCurrentCustomer();

        Order order = orderRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new DataNotFoundException("Order tidak ditemukan"));

        if(!customer.getId().equals(order.getCustomer().getId())){
            throw new AccessDeniedException("Order bukan punya anda");
        }

        if(order.getStatus() != OrderStatus.PENDING){
            throw new BusinessException("Order tidak dapat dibayar");
        }

        Payment payment = paymentRepository.findByOrderIdAndPaidAtIsNull(id)
                .orElseThrow(() -> new DataNotFoundException("Order tidak ditemukan atau telah dibayarkan"));

        if(payment.getPaymentExpiredAt().isBefore(LocalDateTime.now())){
            throw new BusinessException("Waktu pembayaran sudah habis");
        }

        order.setStatus(OrderStatus.PAID);
        orderRepository.save(order);

        payment.setPaidAt(LocalDateTime.now());
        payment.setStatus(PaymentStatus.PAID);
        return paymentRepository.save(payment);
    }

}
