package com.example.do_shopping.repository;

import com.example.do_shopping.entity.Customer;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, String> {
    Optional<Customer> findByIdAndDeletedAtIsNull(String id);

    Optional<Customer> findByUserIdAndDeletedAtIsNull(String userId);

    long countByDeletedAtIsNull();

    @EntityGraph(attributePaths = { "user" })
    @Query("SELECT c FROM Customer c")
    Page<Customer> findAllActiveCustomers(Pageable pageable);
}
