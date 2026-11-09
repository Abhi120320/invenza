package com.invenza;

import com.invenza.dto.payment.CreateOrderRequest;
import com.invenza.dto.payment.CreateOrderResponse;
import com.invenza.dto.payment.PaymentResponse;
import com.invenza.dto.payment.VerifyPaymentRequest;
import com.invenza.entity.*;
import com.invenza.exception.BadRequestException;
import com.invenza.gateway.PaymentGateway;
import com.invenza.repository.OperationRepository;
import com.invenza.repository.PaymentRepository;
import com.invenza.service.PaymentService;
import com.invenza.service.impl.PaymentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private OperationRepository operationRepository;

    @Mock
    private PaymentGateway paymentGateway;

    private PaymentService paymentService;
    private Operation testOperation;
    private UUID opId;

    @BeforeEach
    void setUp() {
        paymentService = new PaymentServiceImpl(paymentRepository, operationRepository, paymentGateway);
        opId = UUID.randomUUID();
        testOperation = new Operation();
        testOperation.setId(opId);
        testOperation.setReference("WH/OUT/00042");
        testOperation.setType(OperationType.OUT);
        testOperation.setStatus(OperationStatus.DONE);
    }

    @Test
    @DisplayName("Should successfully create a Razorpay order with default calculated amount")
    void testCreateOrderSuccess() {
        when(operationRepository.findById(opId)).thenReturn(Optional.of(testOperation));
        when(paymentGateway.createOrder(anyLong(), eq("INR"), anyString(), eq("WH/OUT/00042")))
                .thenReturn("order_test_987654321");
        when(paymentGateway.getKeyId()).thenReturn("rzp_test_TvmS8r4oOuHudO");
        when(paymentRepository.save(any(Payment.class))).thenAnswer(i -> i.getArgument(0));

        CreateOrderRequest request = new CreateOrderRequest(opId, null, "INR");
        CreateOrderResponse response = paymentService.createOrder(request);

        assertNotNull(response);
        assertEquals("order_test_987654321", response.getOrderId());
        assertEquals("rzp_test_TvmS8r4oOuHudO", response.getKeyId());
        assertEquals("INR", response.getCurrency());
        assertEquals(opId, response.getOperationId());
        assertTrue(response.getAmountPaise() > 0);

        verify(paymentRepository, times(1)).save(any(Payment.class));
    }

    @Test
    @DisplayName("Should verify valid signature and update payment status to SUCCESS")
    void testVerifyPaymentValidSignature() {
        String orderId = "order_test_123";
        String paymentId = "pay_test_456";
        String signature = "valid_signature_hash";

        Payment pendingPayment = new Payment(testOperation, orderId, BigDecimal.valueOf(500.00), "INR", "RCPT-001");
        pendingPayment.setId(UUID.randomUUID());

        when(paymentGateway.verifySignature(orderId, paymentId, signature)).thenReturn(true);
        when(paymentRepository.findByRazorpayOrderId(orderId)).thenReturn(Optional.of(pendingPayment));
        when(paymentRepository.save(any(Payment.class))).thenAnswer(i -> i.getArgument(0));

        VerifyPaymentRequest request = new VerifyPaymentRequest(opId, orderId, paymentId, signature);
        PaymentResponse response = paymentService.verifyPayment(request);

        assertNotNull(response);
        assertEquals(PaymentStatus.SUCCESS, response.getStatus());
        assertEquals(paymentId, response.getRazorpayPaymentId());
        assertNotNull(response.getPaidAt());
        assertTrue(pendingPayment.isSuccessful());

        verify(paymentRepository, times(1)).save(pendingPayment);
    }

    @Test
    @DisplayName("Should throw BadRequestException if signature is invalid")
    void testVerifyPaymentInvalidSignature() {
        String orderId = "order_test_123";
        String paymentId = "pay_test_456";
        String signature = "invalid_hash";

        when(paymentGateway.verifySignature(orderId, paymentId, signature)).thenReturn(false);

        VerifyPaymentRequest request = new VerifyPaymentRequest(opId, orderId, paymentId, signature);

        assertThrows(BadRequestException.class, () -> paymentService.verifyPayment(request));
        verify(paymentRepository, never()).save(any());
    }
}

