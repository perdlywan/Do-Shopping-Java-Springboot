package com.example.do_shopping.controller;

import com.example.do_shopping.dto.response.ActionSuccessResponseDTO;
import com.example.do_shopping.dto.response.payment.PaymentResponseDTO;
import com.example.do_shopping.dto.response.payment.PaymentSuccessResponseDTO;
import com.example.do_shopping.entity.Payment;
import com.example.do_shopping.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;

    @PutMapping("/{id}")
    public ResponseEntity<ActionSuccessResponseDTO> doPayment(@PathVariable("id") String id){
        Payment payment = paymentService.doPayment(id);

        PaymentSuccessResponseDTO paymentResponseDTO = new PaymentSuccessResponseDTO(
                payment.getId(),
                payment.getOrder().getId(),
                payment.getMethodType(),
                payment.getProviderName(),
                payment.getAmount(),
                payment.getPaymentExpiredAt(),
                payment.getStatus(),
                payment.getPaidAt()
        );

        ActionSuccessResponseDTO response = new ActionSuccessResponseDTO(
                HttpStatus.OK.value(),
                "Pembayaran berhasil",
                paymentResponseDTO
        );

        return  ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
