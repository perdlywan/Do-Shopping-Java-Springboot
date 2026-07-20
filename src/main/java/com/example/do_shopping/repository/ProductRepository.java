package com.example.do_shopping.repository;

import com.example.do_shopping.entity.Product;

import jakarta.persistence.LockModeType;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;

public interface ProductRepository extends JpaRepository<Product, String> {
        Optional<Product> findByNameAndDeletedAtIsNull(String name);

        Optional<Product> findByIdAndDeletedAtIsNull(String id);

        @Lock(LockModeType.PESSIMISTIC_WRITE)
        @Query("SELECT p FROM Product p WHERE p.id = :id AND p.deletedAt IS NULL")
        Optional<Product> findByIdAndDeletedAtIsNullWithPessimisticLock(
                        @org.springframework.data.repository.query.Param("id") String id);

        @EntityGraph(attributePaths = { "category" })
        @Query("SELECT p FROM Product p WHERE p.deletedAt IS NULL")
        Page<Product> findAllActive(Pageable pageable);

        @EntityGraph(attributePaths = { "category" })
        @Query("SELECT p FROM Product p WHERE p.category.id = :categoryId AND p.deletedAt IS NULL")
        Page<Product> findAllActiveByCategoryId(
                        @org.springframework.data.repository.query.Param("categoryId") String categoryId,
                        Pageable pageable);

        long countByDeletedAtIsNull();

        List<Product> findTop5ByStockLessThanAndDeletedAtIsNullOrderByStockAsc(Integer stockThreshold);

        Long countByStockAndDeletedAtIsNull(Integer stock);

        @Query("SELECT p FROM Product p " +
                        "LEFT JOIN OrderDetail od ON p.id = od.product.id " +
                        "WHERE p.deletedAt IS NULL " +
                        "GROUP BY p " +
                        "ORDER BY COALESCE(SUM(od.quantity), 0) ASC")
        List<Product> findSlowMovingProducts(Pageable pageable);
}
