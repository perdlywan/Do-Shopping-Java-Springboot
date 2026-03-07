package com.example.do_shopping.repository;

import com.example.do_shopping.entity.Shipping;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ShippingRepositroy extends JpaRepository<Shipping, Long> {
    Optional<Shipping> findByOrderIdAndDeletedAtIsNull(Long orderId);
}
