package com.example.do_shopping.service;

import com.example.do_shopping.dto.request.order.AddOrderRequestDTO;
import com.example.do_shopping.dto.request.order.AdminUpdateOrderRequestDTO;
import com.example.do_shopping.dto.request.order.OrderItemRequestDTO;
import com.example.do_shopping.entity.*;
import com.example.do_shopping.enums.OrderStatus;
import com.example.do_shopping.enums.PaymentMethod;
import com.example.do_shopping.enums.PaymentStatus;
import com.example.do_shopping.enums.ShippingStatus;
import com.example.do_shopping.exception.custom.BusinessException;
import com.example.do_shopping.exception.custom.DataNotFoundException;
import com.example.do_shopping.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cglib.core.Local;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final AuthService authService;
    private final OrderRepository orderRepository;
    private final ShippingAddressRepository shippingAddressRepository;
    private final ProductRepository productRepository;
    private final OrderDetailRepository orderDetailRepository;
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
        Order order = orderRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new DataNotFoundException("Data order tidak ditemukan"));

        Customer customer = authService.getCurrentCustomer();

        if (!customer.getId().equals(order.getCustomer().getId())) {
            throw new BusinessException("Order bukan milik anda");
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
                            "Alamat default tidak ditemukan. Silakan pilih alamat atau atur alamat default."));
        } else {
            address = shippingAddressRepository
                    .findByIdAndDeletedAtIsNull(request.getShippingAddressId())
                    .orElseThrow(() -> new DataNotFoundException("Alamat tidak ditemukan"));
        }

        if (!address.getCustomer().getId().equals(customer.getId())) {
            throw new AccessDeniedException("Bukan alamat anda");
        }

        Order order = new Order();
        order.setCustomer(customer);
        order.setOrderDate(LocalDateTime.now());
        order.setNote(request.getNote());
        order.setStatus(OrderStatus.PENDING);

        Integer totalQuantity = 0;
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (OrderItemRequestDTO itemRequest : request.getItems()) {
            Product product = productRepository
                    .findByIdAndDeletedAtIsNull(itemRequest.getProductId())
                    .orElseThrow(() -> new DataNotFoundException("Produk tidak ditemukan"));

            if (product.getStock() < itemRequest.getQuantity()) {
                throw new BusinessException("Stok " + product.getName() + " tidak cukup");
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

        if (request.getPaymentMethod() == PaymentMethod.COD) {
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
        Order order = orderRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new DataNotFoundException("Order tidak ditemukan"));

        if (request.getOrderStatus() != null) {
            order.setStatus(request.getOrderStatus());
            orderRepository.save(order);
        }

        Shipping shipping = shippingRepository.findByOrderIdAndDeletedAtIsNull(order.getId())
                .orElseThrow(() -> new DataNotFoundException("Data pengiriman tidak ditemukan untuk order ini"));

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

        if (request.getShippingStatus() != null) {
            if (request.getShippingStatus() == ShippingStatus.SHIPPED &&
                    (shipping.getTrackingNumber() == null || shipping.getTrackingNumber().isBlank()) &&
                    request.getTrackingNumber() == null) {
                throw new BusinessException("Nomor resi (tracking number) wajib diisi jika status diubah ke SHIPPED");
            }

            shipping.setStatus(request.getShippingStatus());

            if (request.getShippingStatus() == ShippingStatus.SHIPPED && shipping.getShippedAt() == null) {
                shipping.setShippedAt(LocalDateTime.now());
            }

            if (request.getShippingStatus() == ShippingStatus.DELIVERED && shipping.getDeliveredAt() == null) {
                shipping.setDeliveredAt(LocalDateTime.now());
            }
        }

        if (request.getShippedAt() != null) {
            shipping.setShippedAt(request.getShippedAt());
        }

        if (request.getDeliveredAt() != null) {
            shipping.setDeliveredAt(request.getDeliveredAt());
        }

        return shippingRepository.save(shipping);
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @Transactional
    public Shipping cancelOrder(String id) {
        Customer customer = authService.getCurrentCustomer();

        Order order = orderRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new DataNotFoundException("Order tidak ditemukan"));

        if (!customer.getId().equals(order.getCustomer().getId())) {
            throw new AccessDeniedException("Order bukan punya anda");
        }

        Order orderPending = orderRepository.findByIdAndDeletedAtIsNullAndStatus(id, OrderStatus.PENDING)
                .orElseThrow(() -> new BusinessException("Order tidak dapat dicancel"));

        List<OrderDetail> orderDetails = orderDetailRepository.findByOrderIdAndDeletedAtIsNull(orderPending.getId());

        if (orderDetails.isEmpty()) {
            throw new BusinessException("Detail order tidak ditemukan");
        }

        for (OrderDetail orderDetailProduct : orderDetails) {
            productRepository.findByIdAndDeletedAtIsNull(orderDetailProduct.getProduct().getId())
            .ifPresent(product -> {
                int updatedStock = product.getStock() + orderDetailProduct.getQuantity();
                product.setStock(updatedStock);
                productRepository.save(product);
            });
        }

        orderPending.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(orderPending);

        Payment payment = paymentRepository.findByOrderIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new DataNotFoundException("Pembayaran tidak ditemukan"));

        payment.setStatus(PaymentStatus.CANCELLED);
        paymentRepository.save(payment);

        Shipping shipping = shippingRepository.findByOrderIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new DataNotFoundException("Pengiriman tidak ditemukan"));

        shipping.setStatus(ShippingStatus.CANCELLED);
        return shippingRepository.save(shipping);
    }
}
