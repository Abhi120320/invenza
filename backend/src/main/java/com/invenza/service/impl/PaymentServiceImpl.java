package com.invenza.service.impl;

import com.invenza.dto.payment.CreateOrderRequest;
import com.invenza.dto.payment.CreateOrderResponse;
import com.invenza.dto.payment.PaymentResponse;
import com.invenza.dto.payment.VerifyPaymentRequest;
import com.invenza.entity.Operation;
import com.invenza.entity.Payment;
import com.invenza.exception.BadRequestException;
import com.invenza.exception.ResourceNotFoundException;
import com.invenza.gateway.PaymentGateway;
import com.invenza.repository.OperationRepository;
import com.invenza.repository.PaymentRepository;
import com.invenza.service.PaymentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

/**
 * Concrete implementation of PaymentService.
 * Handles order orchestration, amount computation, transaction persistence, and signature verification.
 */
@Service
public class PaymentServiceImpl implements PaymentService {

    private static final Logger log = LoggerFactory.getLogger(PaymentServiceImpl.class);
    private static final BigDecimal DEFAULT_UNIT_PRICE = BigDecimal.valueOf(100.00);

    private final PaymentRepository paymentRepository;
    private final OperationRepository operationRepository;
    private final PaymentGateway paymentGateway;

    public PaymentServiceImpl(
            PaymentRepository paymentRepository,
            OperationRepository operationRepository,
            PaymentGateway paymentGateway) {
        this.paymentRepository = paymentRepository;
        this.operationRepository = operationRepository;
        this.paymentGateway = paymentGateway;
    }

    @Override
    @Transactional
    public CreateOrderResponse createOrder(CreateOrderRequest request) {
        Operation operation = operationRepository.findById(request.getOperationId())
                .orElseThrow(() -> new ResourceNotFoundException("Operation not found with ID: " + request.getOperationId()));

        BigDecimal amount = request.getAmount();
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            amount = calculateOperationAmount(operation);
        }
        amount = amount.setScale(2, RoundingMode.HALF_UP);

        long amountInPaise = amount.multiply(BigDecimal.valueOf(100)).longValue();
        if (amountInPaise <= 0) {
            amountInPaise = 10000; // default ₹100.00
            amount = BigDecimal.valueOf(100.00);
        }

        String currency = request.getCurrency() != null && !request.getCurrency().isBlank()
                ? request.getCurrency().toUpperCase()
                : "INR";

        String receipt = "RCPT-" + operation.getReference().replace("/", "-") + "-" + (System.currentTimeMillis() % 100000);

        String orderId = paymentGateway.createOrder(amountInPaise, currency, receipt, operation.getReference());

        Payment payment = new Payment(operation, orderId, amount, currency, receipt);
        paymentRepository.save(payment);

        return new CreateOrderResponse(
                orderId,
                amount,
                amountInPaise,
                currency,
                paymentGateway.getKeyId(),
                operation.getId(),
                operation.getReference()
        );
    }

    @Override
    @Transactional
    public PaymentResponse verifyPayment(VerifyPaymentRequest request) {
        boolean isValid = paymentGateway.verifySignature(
                request.getRazorpayOrderId(),
                request.getRazorpayPaymentId(),
                request.getRazorpaySignature()
        );

        if (!isValid) {
            throw new BadRequestException("Invalid Razorpay payment signature");
        }

        Payment payment = paymentRepository.findByRazorpayOrderId(request.getRazorpayOrderId())
                .orElseGet(() -> paymentRepository.findTopByOperationIdOrderByCreatedAtDesc(request.getOperationId())
                        .orElseThrow(() -> new ResourceNotFoundException("No payment found for order: " + request.getRazorpayOrderId())));

        payment.markSuccess(request.getRazorpayPaymentId(), request.getRazorpaySignature());
        paymentRepository.save(payment);

        log.info("Payment verified successfully for operation {} with Razorpay Payment ID {}",
                payment.getOperation().getReference(), payment.getRazorpayPaymentId());

        return PaymentResponse.fromEntity(payment);
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentResponse getPaymentForOperation(UUID operationId) {
        return paymentRepository.findTopByOperationIdOrderByCreatedAtDesc(operationId)
                .map(PaymentResponse::fromEntity)
                .orElse(null);
    }

    private BigDecimal calculateOperationAmount(Operation operation) {
        if (operation.getLines() == null || operation.getLines().isEmpty()) {
            return BigDecimal.valueOf(500.00); // Default ₹500
        }

        BigDecimal totalQuantity = operation.getLines().stream()
                .map(l -> l.getQuantity() != null ? l.getQuantity() : BigDecimal.ONE)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (totalQuantity.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.valueOf(100.00);
        }

        return totalQuantity.multiply(DEFAULT_UNIT_PRICE);
    }
}

