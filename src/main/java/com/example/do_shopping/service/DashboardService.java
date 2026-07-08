package com.example.do_shopping.service;

import com.example.do_shopping.dto.response.dashboard.DashboardRecentOrderDTO;
import com.example.do_shopping.dto.response.dashboard.DashboardSummaryResponseDTO;
import com.example.do_shopping.repository.CustomerRepository;
import com.example.do_shopping.repository.OrderRepository;
import com.example.do_shopping.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.stream.Collectors;

@Service
public class DashboardService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @PreAuthorize("hasRole('ADMIN')")
    public DashboardSummaryResponseDTO getDashboardSummary() {
        BigDecimal totalRevenue = orderRepository.sumCompletedOrdersAmount();
        totalRevenue = (totalRevenue == null) ? BigDecimal.ZERO : totalRevenue;

        long totalOrders = orderRepository.countByDeletedAtIsNull();

        long totalCustomers = customerRepository.countByDeletedAtIsNull();

        long totalProducts = productRepository.countByDeletedAtIsNull();

        var recentOrders = orderRepository.findTop5ByDeletedAtIsNullOrderByCreatedAtDesc()
                .stream()
                .map(order -> {
                    var dto = new DashboardRecentOrderDTO();
                    dto.setOrderId(order.getId());
                    dto.setOrderNumber(order.getOrderNumber());
                    dto.setCustomerName(order.getCustomer().getName());
                    dto.setOrderDate(order.getCreatedAt());
                    dto.setTotalAmount(order.getTotalAmount());
                    dto.setStatus(order.getStatus());
                    return dto;
                })
                .collect(Collectors.toList());

        return new DashboardSummaryResponseDTO(
                totalRevenue,
                totalOrders,
                totalCustomers,
                totalProducts,
                recentOrders);
    }
}
