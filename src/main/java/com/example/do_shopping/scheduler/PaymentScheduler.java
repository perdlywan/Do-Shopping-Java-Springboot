package com.example.do_shopping.scheduler;

import com.example.do_shopping.entity.Order;
import com.example.do_shopping.entity.Payment;
import com.example.do_shopping.enums.OrderStatus;
import com.example.do_shopping.enums.PaymentStatus;
import com.example.do_shopping.entity.OrderDetail;
import com.example.do_shopping.enums.ShippingStatus;
import com.example.do_shopping.repository.OrderDetailRepository;
import com.example.do_shopping.repository.OrderRepository;
import com.example.do_shopping.repository.PaymentRepository;
import com.example.do_shopping.repository.ProductRepository;
import com.example.do_shopping.repository.ShippingRepositroy;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class PaymentScheduler {
    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final ShippingRepositroy shippingRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final ProductRepository productRepository;

    @Transactional
    @Scheduled(fixedRate = 300000)
    public void cancelExpiredPayments() {
        List<Payment> expiredPayments = paymentRepository.findByStatusAndPaymentExpiredAtBefore(PaymentStatus.PENDING,
                LocalDateTime.now());

        for (Payment payment : expiredPayments) {
            payment.setStatus(PaymentStatus.EXPIRED);
            paymentRepository.save(payment);

            Order order = payment.getOrder();
            order.setStatus(OrderStatus.CANCELLED);
            orderRepository.save(order);

            shippingRepository
                    .findByOrderIdAndDeletedAtIsNull(order.getId())
                    .ifPresent(shipping -> {
                        shipping.setStatus(ShippingStatus.CANCELLED);
                        shippingRepository.save(shipping);
                    });

            List<OrderDetail> orderDetails = orderDetailRepository.findByOrderIdAndDeletedAtIsNull(order.getId());
            for (OrderDetail orderDetailProduct : orderDetails) {
                productRepository.findByIdAndDeletedAtIsNull(orderDetailProduct.getProduct().getId())
                        .ifPresent(product -> {
                            int updatedStock = product.getStock() + orderDetailProduct.getQuantity();
                            product.setStock(updatedStock);
                            productRepository.save(product);
                        });
            }
        }
    }
}
