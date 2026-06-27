package com.example.do_shopping.repository;

import com.example.do_shopping.entity.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderDetailRepository extends JpaRepository<OrderDetail, String>  {
    List<OrderDetail> findByOrderIdAndDeletedAtIsNull(String id);
}
