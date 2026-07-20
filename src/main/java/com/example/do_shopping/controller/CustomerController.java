package com.example.do_shopping.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.example.do_shopping.dto.response.DataResponseDTO;
import com.example.do_shopping.dto.response.PagedResponseDTO;
import com.example.do_shopping.dto.response.customer.CustomerDetailResponseDTO;
import com.example.do_shopping.dto.response.customer.CustomerResponseDTO;
import com.example.do_shopping.dto.response.order.OrderResponseDTO;
import com.example.do_shopping.entity.Customer;
import com.example.do_shopping.entity.Order;
import com.example.do_shopping.entity.ShippingAddress;
import com.example.do_shopping.repository.OrderRepository;
import com.example.do_shopping.repository.ShippingAddressRepository;
import com.example.do_shopping.service.CustomerService;
import com.example.do_shopping.dto.response.shippingAddress.ShippingAddressResponseDTO;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/customers")
@RequiredArgsConstructor
public class CustomerController {
        private final CustomerService customerService;
        private final OrderRepository orderRepository;
        private final ShippingAddressRepository shippingAddressRepository;

        @GetMapping
        public ResponseEntity<PagedResponseDTO<CustomerResponseDTO>> getAllCustomers(
                        @RequestParam(defaultValue = "1") int page,
                        @RequestParam(defaultValue = "10") int size,
                        @RequestParam(defaultValue = "totalSpent,totalOrders") String sortBy,
                        @RequestParam(defaultValue = "desc") String sortDirection) {
                int pageIndex = page > 0 ? page - 1 : 0;
                Page<Customer> customersPage = customerService.getAllCustomers(pageIndex, size, sortBy, sortDirection);

                List<CustomerResponseDTO> customerResponseDTOs = customersPage.getContent().stream()
                                .map(customer -> new CustomerResponseDTO(
                                                customer.getId(),
                                                customer.getUser().getUsername(),
                                                customer.getName(),
                                                customer.getUser().getEmail(),
                                                customer.getTotalOrders() != null ? customer.getTotalOrders() : 0L,
                                                customer.getTotalSpent() != null ? customer.getTotalSpent()
                                                                : java.math.BigDecimal.ZERO,
                                                customer.getUser().getDeletedAt() != null ? "Inactive" : "Active"))
                                .collect(Collectors.toList());

                PagedResponseDTO<CustomerResponseDTO> response = new PagedResponseDTO<>(
                                HttpStatus.OK.value(),
                                customerResponseDTOs,
                                customersPage.getNumber() + 1,
                                customersPage.getSize(),
                                customersPage.getTotalElements(),
                                customersPage.getTotalPages(),
                                customersPage.isLast());

                return ResponseEntity.status(HttpStatus.OK).body(response);
        }

        @GetMapping("/{id}")
        public ResponseEntity<DataResponseDTO> getCustomerDetail(
                        @PathVariable("id") String id,
                        @RequestParam(defaultValue = "1") int page,
                        @RequestParam(defaultValue = "5") int size) {

                Customer customer = customerService.getCustomerById(id);

                // Get recent orders
                int pageIndex = page > 0 ? page - 1 : 0;
                Pageable pageable = PageRequest.of(pageIndex, size, Sort.by(Sort.Direction.DESC, "createdAt"));
                Page<Order> ordersPage = orderRepository.findByCustomerIdAndDeletedAtIsNull(id, pageable);

                List<OrderResponseDTO> orderDTOs = ordersPage.getContent().stream().map(order -> {
                        return new OrderResponseDTO(
                                        order.getId(),
                                        order.getOrderNumber(),
                                        order.getCustomer().getId(),
                                        order.getOrderDate(),
                                        order.getTotalQuantity(),
                                        order.getTotalAmount(),
                                        order.getNote(),
                                        order.getStatus(),
                                        null, null, null);
                }).collect(Collectors.toList());

                PagedResponseDTO<OrderResponseDTO> recentOrders = new PagedResponseDTO<>(
                                HttpStatus.OK.value(),
                                orderDTOs,
                                ordersPage.getNumber() + 1,
                                ordersPage.getSize(),
                                ordersPage.getTotalElements(),
                                ordersPage.getTotalPages(),
                                ordersPage.isLast());

                List<ShippingAddress> addresses = shippingAddressRepository.findAllByCustomerIdAndDeletedAtIsNull(id);
                List<ShippingAddressResponseDTO> addressDTOs = addresses.stream()
                                .map(addr -> new ShippingAddressResponseDTO(
                                                addr.getId(),
                                                addr.getCustomer().getId(),
                                                addr.getAddress(),
                                                addr.getCountry(),
                                                addr.getState(),
                                                addr.getCity(),
                                                addr.getPostalCode(),
                                                addr.getIsDefault()))
                                .collect(Collectors.toList());

                CustomerDetailResponseDTO detailDTO = new CustomerDetailResponseDTO(
                                customer.getId(),
                                customer.getUser().getUsername(),
                                customer.getName(),
                                customer.getUser().getEmail(),
                                customer.getPhone(),
                                customer.getTotalOrders() != null ? customer.getTotalOrders() : 0L,
                                customer.getTotalSpent() != null ? customer.getTotalSpent() : java.math.BigDecimal.ZERO,
                                customer.getUser().getDeletedAt() != null ? "Inactive" : "Active",
                                customer.getCreatedAt(),
                                recentOrders,
                                addressDTOs);

                return ResponseEntity.status(HttpStatus.OK).body(
                                new DataResponseDTO(HttpStatus.OK.value(), detailDTO));
        }

        @PatchMapping("/{id}/deactivate")
        public ResponseEntity<DataResponseDTO> deactivateCustomer(@PathVariable("id") String id) {
                String adminId = SecurityContextHolder.getContext().getAuthentication().getName();
                customerService.deactivateCustomer(id, adminId);
                return ResponseEntity.status(HttpStatus.OK).body(
                                new DataResponseDTO(HttpStatus.OK.value(), "Customer deactivated successfully"));
        }

        @PatchMapping("/{id}/activate")
        public ResponseEntity<DataResponseDTO> activateCustomer(@PathVariable("id") String id) {
                customerService.activateCustomer(id);
                return ResponseEntity.status(HttpStatus.OK).body(
                                new DataResponseDTO(HttpStatus.OK.value(), "Customer activated successfully"));
        }
}
