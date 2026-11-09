package com.invenza.dto.payment;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.invenza.entity.Payment;
import com.invenza.entity.PaymentStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public class PaymentResponse {

    private UUID id;

    @JsonProperty("operation_id")
    private UUID operationId;

    @JsonProperty("razorpay_order_id")
    private String razorpayOrderId;

    @JsonProperty("razorpay_payment_id")
    private String razorpayPaymentId;

    private BigDecimal amount;

    private String currency;

    private PaymentStatus status;

    @JsonProperty("receipt_number")
    private String receiptNumber;

    @JsonProperty("paid_at")
    private Instant paidAt;

    @JsonProperty("created_at")
    private Instant createdAt;

    public PaymentResponse() {
    }

    public static PaymentResponse fromEntity(Payment payment) {
        if (payment == null) return null;
        PaymentResponse resp = new PaymentResponse();
        resp.setId(payment.getId());
        resp.setOperationId(payment.getOperation() != null ? payment.getOperation().getId() : null);
        resp.setRazorpayOrderId(payment.getRazorpayOrderId());
        resp.setRazorpayPaymentId(payment.getRazorpayPaymentId());
        resp.setAmount(payment.getAmount());
        resp.setCurrency(payment.getCurrency());
        resp.setStatus(payment.getStatus());
        resp.setReceiptNumber(payment.getReceiptNumber());
        resp.setPaidAt(payment.getPaidAt());
        resp.setCreatedAt(payment.getCreatedAt());
        return resp;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getOperationId() {
        return operationId;
    }

    public void setOperationId(UUID operationId) {
        this.operationId = operationId;
    }

    public String getRazorpayOrderId() {
        return razorpayOrderId;
    }

    public void setRazorpayOrderId(String razorpayOrderId) {
        this.razorpayOrderId = razorpayOrderId;
    }

    public String getRazorpayPaymentId() {
        return razorpayPaymentId;
    }

    public void setRazorpayPaymentId(String razorpayPaymentId) {
        this.razorpayPaymentId = razorpayPaymentId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
    }

    public String getReceiptNumber() {
        return receiptNumber;
    }

    public void setReceiptNumber(String receiptNumber) {
        this.receiptNumber = receiptNumber;
    }

    public Instant getPaidAt() {
        return paidAt;
    }

    public void setPaidAt(Instant paidAt) {
        this.paidAt = paidAt;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}

