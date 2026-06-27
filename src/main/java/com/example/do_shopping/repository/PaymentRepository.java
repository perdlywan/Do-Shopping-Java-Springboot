package com.example.do_shopping.repository;

import com.example.do_shopping.entity.Payment;
import com.example.do_shopping.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, String> {
    Optional<Payment> findByOrderIdAndPaidAtIsNull(String id);

    Optional<Payment> findByOrderIdAndDeletedAtIsNull(String id);

    List<Payment> findByStatusAndPaymentExpiredAtBefore(
            PaymentStatus status,
            LocalDateTime time
    );
}
