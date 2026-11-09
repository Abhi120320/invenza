package com.invenza.controller;

import com.invenza.dto.payment.CreateOrderRequest;
import com.invenza.dto.payment.CreateOrderResponse;
import com.invenza.dto.payment.PaymentResponse;
import com.invenza.dto.payment.VerifyPaymentRequest;
import com.invenza.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/create-order")
    public ResponseEntity<CreateOrderResponse> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        CreateOrderResponse response = paymentService.createOrder(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/verify")
    public ResponseEntity<PaymentResponse> verifyPayment(@Valid @RequestBody VerifyPaymentRequest request) {
        PaymentResponse response = paymentService.verifyPayment(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/operation/{operationId}")
    public ResponseEntity<PaymentResponse> getPaymentByOperation(@PathVariable UUID operationId) {
        PaymentResponse response = paymentService.getPaymentForOperation(operationId);
        if (response == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(response);
    }
}

