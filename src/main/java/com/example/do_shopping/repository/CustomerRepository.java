package com.example.do_shopping.repository;

import com.example.do_shopping.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, String> {
    Optional<Customer> findByIdAndDeletedAtIsNull(String id);

    Optional<Customer> findByUserIdAndDeletedAtIsNull(String userId);
}
