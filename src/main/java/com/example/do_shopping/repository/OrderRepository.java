package com.example.do_shopping.repository;

import com.example.do_shopping.entity.Order;
import com.example.do_shopping.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;

public interface OrderRepository extends JpaRepository<Order, String> {
    Optional<Order> findByIdAndDeletedAtIsNull(String id);

    @EntityGraph(attributePaths = { "customer", "payment", "shipping", "shipping.shippingAddress" })
    @Query("SELECT o FROM Order o WHERE o.deletedAt IS NULL")
    Page<Order> findAllActive(Pageable pageable);

    @EntityGraph(attributePaths = { "customer", "payment", "shipping", "shipping.shippingAddress" })
    Page<Order> findByCustomerIdAndDeletedAtIsNull(String id, Pageable pageable);

    Optional<Order> findByIdAndDeletedAtIsNullAndStatus(String id, OrderStatus status);

    @EntityGraph(attributePaths = {
            "customer",
            "payment",
            "shipping",
            "shipping.shippingAddress",
            "orderDetails",
            "orderDetails.product"
    })
    Optional<Order> findWithDetailsByIdAndDeletedAtIsNull(String id);

    Long countByDeletedAtIsNull();

    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o WHERE o.status = 'COMPLETED' AND o.deletedAt IS NULL")
    BigDecimal sumCompletedOrdersAmount();

    List<Order> findTop5ByDeletedAtIsNullOrderByCreatedAtDesc();

    Long countByStatusInAndDeletedAtIsNull(List<OrderStatus> statuses);
}
