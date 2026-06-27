package com.example.do_shopping.repository;

import com.example.do_shopping.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByUsernameAndDeletedAtIsNull(String username);

    Optional<User> findByEmailAndDeletedAtIsNull(String email);

    Optional<User> findByUsernameAndIsActiveAndDeletedAtIsNullOrEmailAndIsActiveAndDeletedAtIsNull(
            String username,
            Integer isActive,
            String email,
            Integer isActive2
    );
}
