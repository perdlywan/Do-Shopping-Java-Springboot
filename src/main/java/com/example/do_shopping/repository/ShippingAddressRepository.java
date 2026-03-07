package com.example.do_shopping.repository;

import com.example.do_shopping.entity.Customer;
import com.example.do_shopping.entity.ShippingAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ShippingAddressRepository extends JpaRepository<ShippingAddress, Long> {
    Optional<ShippingAddress> findByIdAndDeletedAtIsNull(Long id);

    List<ShippingAddress> findAllByCustomerIdAndDeletedAtIsNull(Long customerId);

    @Query("SELECT s FROM ShippingAddress s WHERE s.deletedAt IS NULL")
    List<ShippingAddress> findAllActive();

    Optional<ShippingAddress> findByCustomerIdAndIsDefaultTrueAndDeletedAtIsNull(Long customerId);

}
