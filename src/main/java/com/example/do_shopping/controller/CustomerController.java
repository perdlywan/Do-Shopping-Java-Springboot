package com.example.do_shopping.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.do_shopping.dto.response.PagedResponseDTO;
import com.example.do_shopping.dto.response.customer.CustomerResponseDTO;
import com.example.do_shopping.entity.Customer;
import com.example.do_shopping.service.CustomerService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/customers")
@RequiredArgsConstructor
public class CustomerController {
        private final CustomerService customerService;

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
}
