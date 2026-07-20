package com.example.do_shopping.repository;

import com.example.do_shopping.entity.ShippingAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;

public interface ShippingAddressRepository extends JpaRepository<ShippingAddress, String> {
    Optional<ShippingAddress> findByIdAndDeletedAtIsNull(String id);

    List<ShippingAddress> findAllByCustomerIdAndDeletedAtIsNull(String customerId);

    @EntityGraph(attributePaths = { "customer" })
    Page<ShippingAddress> findAllByCustomerIdAndDeletedAtIsNull(String customerId, Pageable pageable);

    @Query("SELECT s FROM ShippingAddress s WHERE s.deletedAt IS NULL")
    List<ShippingAddress> findAllActive();

    Optional<ShippingAddress> findByCustomerIdAndIsDefaultTrueAndDeletedAtIsNull(String customerId);

}
