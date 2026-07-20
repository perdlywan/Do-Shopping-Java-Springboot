package com.example.do_shopping.service;

import com.example.do_shopping.dto.response.dashboard.DashboardLowStockProductDTO;
import com.example.do_shopping.dto.response.dashboard.DashboardSummaryResponseDTO;
import com.example.do_shopping.repository.CustomerRepository;
import com.example.do_shopping.repository.OrderRepository;
import com.example.do_shopping.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.stream.Collectors;
import com.example.do_shopping.enums.OrderStatus;

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

                var lowStockProducts = productRepository.findTop5ByStockLessThanAndDeletedAtIsNullOrderByStockAsc(10)
                                .stream()
                                .map(product -> new DashboardLowStockProductDTO(
                                                product.getId(),
                                                product.getName(),
                                                product.getCategory().getName(),
                                                product.getStock()))
                                .collect(Collectors.toList());

                var slowMovingProducts = productRepository.findSlowMovingProducts(PageRequest.of(0, 5))
                                .stream()
                                .map(product -> new DashboardLowStockProductDTO(
                                                product.getId(),
                                                product.getName(),
                                                product.getCategory().getName(),
                                                product.getStock()))
                                .collect(Collectors.toList());

                Long pendingOrders = orderRepository.countByStatusInAndDeletedAtIsNull(
                                Arrays.asList(OrderStatus.PENDING, OrderStatus.PAID));
                Long outOfStockProducts = productRepository.countByStockAndDeletedAtIsNull(0);

                return new DashboardSummaryResponseDTO(
                                totalRevenue,
                                totalOrders,
                                totalCustomers,
                                totalProducts,
                                lowStockProducts,
                                pendingOrders,
                                outOfStockProducts,
                                slowMovingProducts);
        }
}
