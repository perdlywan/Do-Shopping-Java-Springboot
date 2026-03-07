package com.example.do_shopping.repository;

import com.example.do_shopping.entity.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long>  {
}
