package com.example.do_shopping.controller;

import com.example.do_shopping.dto.request.order.AddOrderRequestDTO;
import com.example.do_shopping.dto.request.order.AdminUpdateOrderRequestDTO;
import com.example.do_shopping.dto.response.ActionSuccessResponseDTO;
import com.example.do_shopping.dto.response.DataResponseDTO;
import com.example.do_shopping.dto.response.category.CategoryResponseDTO;
import com.example.do_shopping.dto.response.order.OrderDetailResponseDTO;
import com.example.do_shopping.dto.response.order.OrderResponseDTO;
import com.example.do_shopping.dto.response.payment.PaymentResponseDTO;
import com.example.do_shopping.dto.response.shipping.ShippingResponseDTO;
import com.example.do_shopping.dto.response.shippingAddress.ShippingAddressResponseDTO;
import com.example.do_shopping.entity.Order;
import com.example.do_shopping.entity.Payment;
import com.example.do_shopping.entity.Shipping;
import com.example.do_shopping.entity.ShippingAddress;
import com.example.do_shopping.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @GetMapping
    public ResponseEntity<DataResponseDTO> getOrders(){
        List<Order> orders =  orderService.getAllOrders();

        List<OrderResponseDTO> orderResponseDTO = orders.stream()
                .map(order -> {
                    List<OrderDetailResponseDTO> detailsDTO = order.getOrderDetails().stream()
                            .map(detail -> new OrderDetailResponseDTO(
                                    detail.getId(),
                                    detail.getProduct().getId(),
                                    detail.getProductName(),
                                    detail.getPrice(),
                                    detail.getQuantity(),
                                    detail.getSubTotal()
                            ))
                            .collect(Collectors.toList());

                    PaymentResponseDTO paymentDTO = null;
                    if (order.getPayment() != null) {
                        Payment payment = order.getPayment();
                        paymentDTO = new PaymentResponseDTO(
                                payment.getId(),
                                payment.getMethodType(),
                                payment.getProviderName(),
                                payment.getAmount(),
                                payment.getPaymentExpiredAt(),
                                payment.getStatus(),
                                payment.getPaidAt()
                        );
                    }

                    ShippingResponseDTO shippingDTO = null;
                    if (order.getShipping() != null) {
                        Shipping shipping = order.getShipping();

                        ShippingAddressResponseDTO addressDTO = null;
                        if (shipping.getShippingAddress() != null) {
                            ShippingAddress addr = shipping.getShippingAddress();
                            addressDTO = new ShippingAddressResponseDTO(
                                    addr.getId(),
                                    addr.getCustomer().getId(),
                                    addr.getAddress(),
                                    addr.getCountry(),
                                    addr.getState(),
                                    addr.getCity(),
                                    addr.getPostalCode(),
                                    addr.getIsDefault()
                            );
                        }

                        shippingDTO = new ShippingResponseDTO(
                                shipping.getId(),
                                addressDTO,
                                shipping.getCourierName(),
                                shipping.getServiceType(),
                                shipping.getTrackingNumber(),
                                shipping.getShippingCost(),
                                shipping.getStatus(),
                                shipping.getShippedAt(),
                                shipping.getDeliveredAt()
                        );
                    }

                    return new OrderResponseDTO(
                            order.getId(),
                            order.getCustomer().getId(),
                            order.getOrderDate(),
                            order.getTotalQuantity(),
                            order.getTotalAmount(),
                            order.getNote(),
                            order.getStatus(),
                            detailsDTO,
                            paymentDTO,
                            shippingDTO
                    );
                })
                .collect(Collectors.toList());

        DataResponseDTO response = new DataResponseDTO(
                HttpStatus.OK.value(),
                orderResponseDTO
        );

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DataResponseDTO> getOrderById(@PathVariable("id") Long id){
        Order order = orderService.getOrderById(id);

        List<OrderDetailResponseDTO> detailsDTO = order.getOrderDetails().stream()
                .map(detail -> new OrderDetailResponseDTO(
                        detail.getId(),
                        detail.getProduct().getId(),
                        detail.getProductName(),
                        detail.getPrice(),
                        detail.getQuantity(),
                        detail.getSubTotal()
                ))
                .collect(Collectors.toList());

        PaymentResponseDTO paymentDTO = null;
        if (order.getPayment() != null) {
            Payment payment = order.getPayment();
            paymentDTO = new PaymentResponseDTO(
                    payment.getId(),
                    payment.getMethodType(),
                    payment.getProviderName(),
                    payment.getAmount(),
                    payment.getPaymentExpiredAt(),
                    payment.getStatus(),
                    payment.getPaidAt()
            );
        }

        ShippingResponseDTO shippingDTO = null;
        if (order.getShipping() != null) {
            Shipping shipping = order.getShipping();

            ShippingAddressResponseDTO addressDTO = null;
            if (shipping.getShippingAddress() != null) {
                ShippingAddress addr = shipping.getShippingAddress();
                addressDTO = new ShippingAddressResponseDTO(
                        addr.getId(),
                        addr.getCustomer().getId(),
                        addr.getAddress(),
                        addr.getCountry(),
                        addr.getState(),
                        addr.getCity(),
                        addr.getPostalCode(),
                        addr.getIsDefault()
                );
            }

            shippingDTO = new ShippingResponseDTO(
                    shipping.getId(),
                    addressDTO,
                    shipping.getCourierName(),
                    shipping.getServiceType(),
                    shipping.getTrackingNumber(),
                    shipping.getShippingCost(),
                    shipping.getStatus(),
                    shipping.getShippedAt(),
                    shipping.getDeliveredAt()
            );
        }

        OrderResponseDTO orderResponseDTO = new OrderResponseDTO(
                order.getId(),
                order.getCustomer().getId(),
                order.getOrderDate(),
                order.getTotalQuantity(),
                order.getTotalAmount(),
                order.getNote(),
                order.getStatus(),
                detailsDTO,
                paymentDTO,
                shippingDTO
        );

        DataResponseDTO response = new DataResponseDTO(
                HttpStatus.OK.value(),
                orderResponseDTO
        );

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/my-orders")
    public ResponseEntity<DataResponseDTO> getMyOrders(){
        List<Order> orders = orderService.getCustomerOrders();

        List<OrderResponseDTO> orderResponseDTO = orders.stream()
                .map(order -> {
                    List<OrderDetailResponseDTO> detailsDTO = order.getOrderDetails().stream()
                            .map(detail -> new OrderDetailResponseDTO(
                                    detail.getId(),
                                    detail.getProduct().getId(),
                                    detail.getProductName(),
                                    detail.getPrice(),
                                    detail.getQuantity(),
                                    detail.getSubTotal()
                            ))
                            .collect(Collectors.toList());

                    PaymentResponseDTO paymentDTO = null;
                    if (order.getPayment() != null) {
                        Payment payment = order.getPayment();
                        paymentDTO = new PaymentResponseDTO(
                                payment.getId(),
                                payment.getMethodType(),
                                payment.getProviderName(),
                                payment.getAmount(),
                                payment.getPaymentExpiredAt(),
                                payment.getStatus(),
                                payment.getPaidAt()
                        );
                    }

                    ShippingResponseDTO shippingDTO = null;
                    if (order.getShipping() != null) {
                        Shipping shipping = order.getShipping();

                        ShippingAddressResponseDTO addressDTO = null;
                        if (shipping.getShippingAddress() != null) {
                            ShippingAddress addr = shipping.getShippingAddress();
                            addressDTO = new ShippingAddressResponseDTO(
                                    addr.getId(),
                                    addr.getCustomer().getId(),
                                    addr.getAddress(),
                                    addr.getCountry(),
                                    addr.getState(),
                                    addr.getCity(),
                                    addr.getPostalCode(),
                                    addr.getIsDefault()
                            );
                        }

                        shippingDTO = new ShippingResponseDTO(
                                shipping.getId(),
                                addressDTO,
                                shipping.getCourierName(),
                                shipping.getServiceType(),
                                shipping.getTrackingNumber(),
                                shipping.getShippingCost(),
                                shipping.getStatus(),
                                shipping.getShippedAt(),
                                shipping.getDeliveredAt()
                        );
                    }

                    return new OrderResponseDTO(
                            order.getId(),
                            order.getCustomer().getId(),
                            order.getOrderDate(),
                            order.getTotalQuantity(),
                            order.getTotalAmount(),
                            order.getNote(),
                            order.getStatus(),
                            detailsDTO,
                            paymentDTO,
                            shippingDTO
                    );
                })
                .collect(Collectors.toList());

        DataResponseDTO response = new DataResponseDTO(
                HttpStatus.OK.value(),
                orderResponseDTO
        );

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping
    public ResponseEntity<ActionSuccessResponseDTO> addOrder(@Valid @RequestBody AddOrderRequestDTO request){
         Shipping shipping = orderService.addOrder(request);

         Order order = shipping.getOrder();

        List<OrderDetailResponseDTO> detailsDTO = order.getOrderDetails().stream()
                .map(detail -> new OrderDetailResponseDTO(
                        detail.getId(),
                        detail.getProduct().getId(),
                        detail.getProductName(),
                        detail.getPrice(),
                        detail.getQuantity(),
                        detail.getSubTotal()
                ))
                .collect(Collectors.toList());

        PaymentResponseDTO paymentDTO = null;
        if (order.getPayment() != null) {
            Payment payment = order.getPayment();
            paymentDTO = new PaymentResponseDTO(
                    payment.getId(),
                    payment.getMethodType(),
                    payment.getProviderName(),
                    payment.getAmount(),
                    payment.getPaymentExpiredAt(),
                    payment.getStatus(),
                    payment.getPaidAt()
            );
        }

        ShippingResponseDTO shippingDTO = null;
        ShippingAddress addr = shipping.getShippingAddress();

        ShippingAddressResponseDTO addressDTO = null;
        if (addr != null) {
            addressDTO = new ShippingAddressResponseDTO(
                    addr.getId(),
                    addr.getCustomer().getId(),
                    addr.getAddress(),
                    addr.getCountry(),
                    addr.getState(),
                    addr.getCity(),
                    addr.getPostalCode(),
                    addr.getIsDefault()
            );
        }

        shippingDTO = new ShippingResponseDTO(
                shipping.getId(),
                addressDTO,
                shipping.getCourierName(),
                shipping.getServiceType(),
                shipping.getTrackingNumber(),
                shipping.getShippingCost(),
                shipping.getStatus(),
                shipping.getShippedAt(),
                shipping.getDeliveredAt()
        );

        OrderResponseDTO orderResponseDTO = new OrderResponseDTO(
                order.getId(),
                order.getCustomer().getId(),
                order.getOrderDate(),
                order.getTotalQuantity(),
                order.getTotalAmount(),
                order.getNote(),
                order.getStatus(),
                detailsDTO,
                paymentDTO,
                shippingDTO
        );

        ActionSuccessResponseDTO response = new ActionSuccessResponseDTO(
                HttpStatus.CREATED.value(),
                "Order berhasil dibuat",
                orderResponseDTO
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);

    }

    @PutMapping("/{id}")
    public ResponseEntity<ActionSuccessResponseDTO> updateOrder(@PathVariable("id") Long id, @Valid @RequestBody AdminUpdateOrderRequestDTO request){
        Shipping shipping = orderService.updateOrder(id, request);

        Order order = shipping.getOrder();

        List<OrderDetailResponseDTO> detailsDTO = order.getOrderDetails().stream()
                .map(detail -> new OrderDetailResponseDTO(
                        detail.getId(),
                        detail.getProduct().getId(),
                        detail.getProductName(),
                        detail.getPrice(),
                        detail.getQuantity(),
                        detail.getSubTotal()
                ))
                .collect(Collectors.toList());

        PaymentResponseDTO paymentDTO = null;
        if (order.getPayment() != null) {
            Payment payment = order.getPayment();
            paymentDTO = new PaymentResponseDTO(
                    payment.getId(),
                    payment.getMethodType(),
                    payment.getProviderName(),
                    payment.getAmount(),
                    payment.getPaymentExpiredAt(),
                    payment.getStatus(),
                    payment.getPaidAt()
            );
        }

        ShippingResponseDTO shippingDTO = null;
        ShippingAddress addr = shipping.getShippingAddress();

        ShippingAddressResponseDTO addressDTO = null;
        if (addr != null) {
            addressDTO = new ShippingAddressResponseDTO(
                    addr.getId(),
                    addr.getCustomer().getId(),
                    addr.getAddress(),
                    addr.getCountry(),
                    addr.getState(),
                    addr.getCity(),
                    addr.getPostalCode(),
                    addr.getIsDefault()
            );
        }

        shippingDTO = new ShippingResponseDTO(
                shipping.getId(),
                addressDTO,
                shipping.getCourierName(),
                shipping.getServiceType(),
                shipping.getTrackingNumber(),
                shipping.getShippingCost(),
                shipping.getStatus(),
                shipping.getShippedAt(),
                shipping.getDeliveredAt()
        );

        OrderResponseDTO orderResponseDTO = new OrderResponseDTO(
                order.getId(),
                order.getCustomer().getId(),
                order.getOrderDate(),
                order.getTotalQuantity(),
                order.getTotalAmount(),
                order.getNote(),
                order.getStatus(),
                detailsDTO,
                paymentDTO,
                shippingDTO
        );

        ActionSuccessResponseDTO response = new ActionSuccessResponseDTO(
                HttpStatus.OK.value(),
                "Order berhasil diupdate",
                orderResponseDTO
        );

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PutMapping("/cancel/{id}")
    public ResponseEntity<ActionSuccessResponseDTO> cancelOrder(@PathVariable("id") Long id){
        Shipping shipping = orderService.cancelOrder(id);

        Order order = shipping.getOrder();

        List<OrderDetailResponseDTO> detailsDTO = order.getOrderDetails().stream()
                .map(detail -> new OrderDetailResponseDTO(
                        detail.getId(),
                        detail.getProduct().getId(),
                        detail.getProductName(),
                        detail.getPrice(),
                        detail.getQuantity(),
                        detail.getSubTotal()
                ))
                .collect(Collectors.toList());

        PaymentResponseDTO paymentDTO = null;
        if (order.getPayment() != null) {
            Payment payment = order.getPayment();
            paymentDTO = new PaymentResponseDTO(
                    payment.getId(),
                    payment.getMethodType(),
                    payment.getProviderName(),
                    payment.getAmount(),
                    payment.getPaymentExpiredAt(),
                    payment.getStatus(),
                    payment.getPaidAt()
            );
        }

        ShippingResponseDTO shippingDTO = null;
        ShippingAddress addr = shipping.getShippingAddress();

        ShippingAddressResponseDTO addressDTO = null;
        if (addr != null) {
            addressDTO = new ShippingAddressResponseDTO(
                    addr.getId(),
                    addr.getCustomer().getId(),
                    addr.getAddress(),
                    addr.getCountry(),
                    addr.getState(),
                    addr.getCity(),
                    addr.getPostalCode(),
                    addr.getIsDefault()
            );
        }

        shippingDTO = new ShippingResponseDTO(
                shipping.getId(),
                addressDTO,
                shipping.getCourierName(),
                shipping.getServiceType(),
                shipping.getTrackingNumber(),
                shipping.getShippingCost(),
                shipping.getStatus(),
                shipping.getShippedAt(),
                shipping.getDeliveredAt()
        );

        OrderResponseDTO orderResponseDTO = new OrderResponseDTO(
                order.getId(),
                order.getCustomer().getId(),
                order.getOrderDate(),
                order.getTotalQuantity(),
                order.getTotalAmount(),
                order.getNote(),
                order.getStatus(),
                detailsDTO,
                paymentDTO,
                shippingDTO
        );

        ActionSuccessResponseDTO response = new ActionSuccessResponseDTO(
                HttpStatus.OK.value(),
                "Order berhasil dibatalkan",
                orderResponseDTO
        );

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
