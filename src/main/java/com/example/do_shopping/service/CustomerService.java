package com.example.do_shopping.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.example.do_shopping.dto.response.customer.CustomerDetailResponseDTO;
import com.example.do_shopping.entity.Customer;
import com.example.do_shopping.entity.ShippingAddress;
import com.example.do_shopping.entity.User;
import com.example.do_shopping.exception.custom.DataNotFoundException;
import com.example.do_shopping.repository.CustomerRepository;
import com.example.do_shopping.repository.OrderRepository;
import com.example.do_shopping.repository.ShippingAddressRepository;
import com.example.do_shopping.dto.response.PagedResponseDTO;
import com.example.do_shopping.dto.response.order.OrderResponseDTO;
import com.example.do_shopping.dto.response.shippingAddress.ShippingAddressResponseDTO;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import com.example.do_shopping.entity.Order;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomerService {
    private final CustomerRepository customerRepository;
    private final OrderRepository orderRepository;
    private final ShippingAddressRepository shippingAddressRepository;

    @PreAuthorize("hasRole('ADMIN')")
    public Page<Customer> getAllCustomers(int page, int size, String sortBy, String sortDirection) {
        String[] properties = sortBy.split(",");
        Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(properties).ascending()
                : Sort.by(properties).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return customerRepository.findAllActiveCustomers(pageable);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public Customer getCustomerById(String id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Customer not found"));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public void deactivateCustomer(String id, String adminId) {
        Customer customer = getCustomerById(id);
        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        customer.setDeletedAt(now);
        customer.setDeletedBy(adminId);
        customerRepository.save(customer);

        User user = customer.getUser();
        if (user != null) {
            user.setDeletedAt(now);
            user.setDeletedBy(adminId);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public void activateCustomer(String id) {
        Customer customer = getCustomerById(id);
        customer.setDeletedAt(null);
        customer.setDeletedBy(null);
        customerRepository.save(customer);

        User user = customer.getUser();
        if (user != null) {
            user.setDeletedAt(null);
            user.setDeletedBy(null);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    public CustomerDetailResponseDTO getCustomerDetail(String id, int page, int size) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Customer not found"));

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

        return new CustomerDetailResponseDTO(
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
    }
}
