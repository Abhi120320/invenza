package com.invenza.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Domain entity representing a Razorpay invoice payment transaction.
 * Demonstrates OOP Encapsulation and Domain Invariants.
 */
@Entity
@Table(name = "payments", indexes = {
    @Index(name = "ix_payments_order_id", columnList = "razorpay_order_id", unique = true),
    @Index(name = "ix_payments_payment_id", columnList = "razorpay_payment_id"),
    @Index(name = "ix_payments_operation_id", columnList = "operation_id")
})
public class Payment extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "operation_id", nullable = false)
    private Operation operation;

    @Column(name = "razorpay_order_id", length = 100, nullable = false, unique = true)
    private String razorpayOrderId;

    @Column(name = "razorpay_payment_id", length = 100)
    private String razorpayPaymentId;

    @Column(name = "razorpay_signature", length = 255)
    private String razorpaySignature;

    @Column(name = "amount", precision = 12, scale = 2, nullable = false)
    private BigDecimal amount;

    @Column(name = "currency", length = 10, nullable = false)
    private String currency = "INR";

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private PaymentStatus status = PaymentStatus.PENDING;

    @Column(name = "receipt_number", length = 100)
    private String receiptNumber;

    @Column(name = "paid_at")
    private Instant paidAt;

    @Column(name = "failure_reason", length = 500)
    private String failureReason;

    public Payment() {
    }

    public Payment(UUID id) {
        super(id);
    }

    public Payment(Operation operation, String razorpayOrderId, BigDecimal amount, String currency, String receiptNumber) {
        this.operation = operation;
        this.razorpayOrderId = razorpayOrderId;
        this.amount = amount;
        this.currency = currency != null ? currency : "INR";
        this.receiptNumber = receiptNumber;
        this.status = PaymentStatus.PENDING;
    }

    // OOP Domain methods
    public void markSuccess(String razorpayPaymentId, String razorpaySignature) {
        if (this.status == PaymentStatus.SUCCESS) {
            return; // idempotent
        }
        this.razorpayPaymentId = razorpayPaymentId;
        this.razorpaySignature = razorpaySignature;
        this.status = PaymentStatus.SUCCESS;
        this.paidAt = Instant.now();
    }

    public void markFailed(String reason) {
        this.status = PaymentStatus.FAILED;
        this.failureReason = reason;
    }

    public boolean isSuccessful() {
        return this.status == PaymentStatus.SUCCESS;
    }

    // Getters and Setters
    public Operation getOperation() {
        return operation;
    }

    public void setOperation(Operation operation) {
        this.operation = operation;
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

    public String getRazorpaySignature() {
        return razorpaySignature;
    }

    public void setRazorpaySignature(String razorpaySignature) {
        this.razorpaySignature = razorpaySignature;
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

    public String getFailureReason() {
        return failureReason;
    }

    public void setFailureReason(String failureReason) {
        this.failureReason = failureReason;
    }
}

