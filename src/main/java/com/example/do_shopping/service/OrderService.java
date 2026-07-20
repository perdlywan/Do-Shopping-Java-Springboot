package com.example.do_shopping.service;

import com.example.do_shopping.dto.request.order.AddOrderRequestDTO;
import com.example.do_shopping.dto.request.order.AdminUpdateOrderRequestDTO;
import com.example.do_shopping.dto.request.order.OrderItemRequestDTO;
import com.example.do_shopping.entity.*;
import com.example.do_shopping.enums.OrderStatus;
import com.example.do_shopping.enums.PaymentMethod;
import com.example.do_shopping.enums.PaymentStatus;
import com.example.do_shopping.enums.Role;
import com.example.do_shopping.enums.ShippingStatus;
import com.example.do_shopping.exception.custom.BusinessException;
import com.example.do_shopping.exception.custom.DataNotFoundException;
import com.example.do_shopping.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final AuthService authService;
    private final OrderRepository orderRepository;
    private final ShippingAddressRepository shippingAddressRepository;
    private final ProductRepository productRepository;
    private final ShippingRepositroy shippingRepository;
    private final PaymentRepository paymentRepository;

    @PreAuthorize("hasRole('ADMIN')")
    public Page<Order> getAllOrders(int pageIndex, int size, String sortBy, String sortDirection) {
        Sort.Direction direction = sortDirection.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(pageIndex, size, Sort.by(direction, sortBy));
        return orderRepository.findAllActive(pageable);
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    public Page<Order> getCustomerOrders(int pageIndex, int size, String sortBy, String sortDirection) {
        Customer customer = authService.getCurrentCustomer();
        Sort.Direction direction = sortDirection.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(pageIndex, size, Sort.by(direction, sortBy));

        return orderRepository.findByCustomerIdAndDeletedAtIsNull(customer.getId(), pageable);
    }

    public Order getOrderById(String id) {
        Order order = orderRepository.findWithDetailsByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new DataNotFoundException("Order data not found"));

        User user = authService.getCurrentUser();

        if (!Role.ADMIN.equals(user.getRole())) {
            Customer customer = authService.getCurrentCustomer();
            if (!customer.getId().equals(order.getCustomer().getId())) {
                throw new BusinessException("Order does not belong to you");
            }
        }

        return order;
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @Transactional
    public Shipping addOrder(AddOrderRequestDTO request) {
        Customer customer = authService.getCurrentCustomer();
        ShippingAddress address;

        if (request.getShippingAddressId() == null) {
            address = shippingAddressRepository
                    .findByCustomerIdAndIsDefaultTrueAndDeletedAtIsNull(customer.getId())
                    .orElseThrow(() -> new DataNotFoundException(
                            "Default address not found. Please select an address or set a default address."));
        } else {
            address = shippingAddressRepository
                    .findByIdAndDeletedAtIsNull(request.getShippingAddressId())
                    .orElseThrow(() -> new DataNotFoundException("Address not found"));
        }

        if (!address.getCustomer().getId().equals(customer.getId())) {
            throw new AccessDeniedException("Not your address");
        }

        Order order = new Order();
        order.setCustomer(customer);
        order.setOrderDate(LocalDateTime.now());
        order.setNote(request.getNote());
        order.setStatus(OrderStatus.PENDING);

        String orderNumber = "ORD-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + "-"
                + UUID.randomUUID().toString().substring(0, 4).toUpperCase();
        order.setOrderNumber(orderNumber);

        Integer totalQuantity = 0;
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (OrderItemRequestDTO itemRequest : request.getItems()) {
            Product product = productRepository
                    .findByIdAndDeletedAtIsNullWithPessimisticLock(itemRequest.getProductId())
                    .orElseThrow(() -> new DataNotFoundException("Product not found"));

            if (product.getStock() < itemRequest.getQuantity()) {
                throw new BusinessException("Stock for " + product.getName() + " is insufficient");
            }

            product.setStock(product.getStock() - itemRequest.getQuantity());
            productRepository.save(product);

            BigDecimal subTotal = product.getPrice().multiply(BigDecimal.valueOf(itemRequest.getQuantity()));

            totalAmount = totalAmount.add(subTotal);

            totalQuantity += itemRequest.getQuantity();

            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrder(order);
            orderDetail.setProduct(product);
            orderDetail.setProductName(product.getName());
            orderDetail.setPrice(product.getPrice());
            orderDetail.setQuantity(itemRequest.getQuantity());
            orderDetail.setSubTotal(subTotal);

            order.addDetail(orderDetail);
        }

        order.setTotalQuantity(totalQuantity);
        order.setTotalAmount(totalAmount);
        orderRepository.save(order);

        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setMethodType(request.getPaymentMethod());
        payment.setProviderName(request.getProviderName());
        payment.setAmount(totalAmount);

        if (PaymentMethod.COD.equals(request.getPaymentMethod())) {
            payment.setPaymentExpiredAt(null);
        } else {
            payment.setPaymentExpiredAt(order.getOrderDate().plusHours(5));
        }

        payment.setStatus(PaymentStatus.PENDING);
        paymentRepository.save(payment);

        Shipping shipping = new Shipping();
        shipping.setOrder(order);

        shipping.setShippingAddress(address);

        shipping.setStatus(ShippingStatus.PENDING);
        return shippingRepository.save(shipping);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public Shipping updateOrder(String id, AdminUpdateOrderRequestDTO request) {
        Order order = orderRepository.findWithDetailsByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new DataNotFoundException("Order not found"));

        if (request.getOrderStatus() != null && request.getOrderStatus() != order.getStatus()) {
            if (order.getStatus() == OrderStatus.COMPLETED || order.getStatus() == OrderStatus.CANCELLED) {
                throw new BusinessException("Cannot change status of a COMPLETED or CANCELLED order");
            }
            order.setStatus(request.getOrderStatus());
        }

        Shipping shipping = order.getShipping();
        if (shipping == null || shipping.getDeletedAt() != null) {
            throw new DataNotFoundException("Shipping data not found for this order");
        }

        if (request.getCourierName() != null && !request.getCourierName().isBlank()) {
            shipping.setCourierName(request.getCourierName());
        }

        if (request.getServiceType() != null && !request.getServiceType().isBlank()) {
            shipping.setServiceType(request.getServiceType());
        }

        if (request.getTrackingNumber() != null && !request.getTrackingNumber().isBlank()) {
            shipping.setTrackingNumber(request.getTrackingNumber());
        }

        if (request.getShippingCost() != null && request.getShippingCost().compareTo(BigDecimal.ZERO) >= 0) {
            shipping.setShippingCost(request.getShippingCost());
        }

        if (request.getShippingStatus() != null && request.getShippingStatus() != shipping.getStatus()) {
            if (shipping.getStatus() == ShippingStatus.DELIVERED || shipping.getStatus() == ShippingStatus.CANCELLED) {
                throw new BusinessException("Cannot change shipping status of a DELIVERED or CANCELLED order");
            }

            if (ShippingStatus.SHIPPED.equals(request.getShippingStatus()) &&
                    (shipping.getTrackingNumber() == null || shipping.getTrackingNumber().isBlank()) &&
                    request.getTrackingNumber() == null) {
                throw new BusinessException("Tracking number is required if status is changed to SHIPPED");
            }

            shipping.setStatus(request.getShippingStatus());

            if (ShippingStatus.SHIPPED.equals(request.getShippingStatus()) && shipping.getShippedAt() == null) {
                shipping.setShippedAt(LocalDateTime.now());
            }

            if (ShippingStatus.DELIVERED.equals(request.getShippingStatus()) && shipping.getDeliveredAt() == null) {
                shipping.setDeliveredAt(LocalDateTime.now());
            }
        }

        if (request.getShippedAt() != null) {
            shipping.setShippedAt(request.getShippedAt());
        }

        if (request.getDeliveredAt() != null) {
            shipping.setDeliveredAt(request.getDeliveredAt());
        }

        return shipping;
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @Transactional
    public Shipping cancelOrder(String id) {
        Customer customer = authService.getCurrentCustomer();

        Order order = orderRepository.findWithDetailsByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new DataNotFoundException("Order not found"));

        if (!customer.getId().equals(order.getCustomer().getId())) {
            throw new AccessDeniedException("Order does not belong to you");
        }

        if (!OrderStatus.PENDING.equals(order.getStatus())) {
            throw new BusinessException("Order cannot be canceled");
        }

        if (order.getOrderDetails().isEmpty()) {
            throw new BusinessException("Order details not found");
        }

        for (OrderDetail detail : order.getOrderDetails()) {
            Product product = detail.getProduct();
            product.setStock(product.getStock() + detail.getQuantity());
        }

        order.setStatus(OrderStatus.CANCELLED);

        if (order.getPayment() != null) {
            order.getPayment().setStatus(PaymentStatus.CANCELLED);
        }

        if (order.getShipping() != null) {
            order.getShipping().setStatus(ShippingStatus.CANCELLED);
        }

        return order.getShipping();
    }
}
