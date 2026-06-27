package com.example.do_shopping.repository;

import com.example.do_shopping.entity.Order;
import com.example.do_shopping.entity.Product;
import com.example.do_shopping.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, String> {
    Optional<Order> findByIdAndDeletedAtIsNull(String id);

    @Query("SELECT o FROM Order o WHERE o.deletedAt IS NULL")
    List<Order> findAllActive();

    List<Order> findByCustomerIdAndDeletedAtIsNull(String id);

    Optional<Order> findByIdAndDeletedAtIsNullAndStatus(String id, OrderStatus status);
}
