package com.example.do_shopping.repository;

import com.example.do_shopping.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, String> {
    Optional<Category> findByNameAndDeletedAtIsNull(String name);

    Optional<Category> findByIdAndDeletedAtIsNull(String id);

    @Query("SELECT c FROM Category c WHERE c.deletedAt IS NULL")
    List<Category> findAllActive();
}
