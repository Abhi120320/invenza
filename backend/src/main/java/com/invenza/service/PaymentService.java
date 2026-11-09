package com.invenza.service;

import com.invenza.dto.payment.CreateOrderRequest;
import com.invenza.dto.payment.CreateOrderResponse;
import com.invenza.dto.payment.PaymentResponse;
import com.invenza.dto.payment.VerifyPaymentRequest;

import java.util.UUID;

/**
 * Service interface for payment and invoice checkout operations.
 * Demonstrates OOP Service Layer abstraction.
 */
public interface PaymentService {

    CreateOrderResponse createOrder(CreateOrderRequest request);

    PaymentResponse verifyPayment(VerifyPaymentRequest request);

    PaymentResponse getPaymentForOperation(UUID operationId);
}

