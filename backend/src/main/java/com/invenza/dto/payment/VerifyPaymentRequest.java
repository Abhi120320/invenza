package com.invenza.dto.payment;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public class VerifyPaymentRequest {

    @NotNull(message = "operation_id is required")
    @JsonProperty("operation_id")
    private UUID operationId;

    @NotBlank(message = "razorpay_order_id is required")
    @JsonProperty("razorpay_order_id")
    private String razorpayOrderId;

    @NotBlank(message = "razorpay_payment_id is required")
    @JsonProperty("razorpay_payment_id")
    private String razorpayPaymentId;

    @NotBlank(message = "razorpay_signature is required")
    @JsonProperty("razorpay_signature")
    private String razorpaySignature;

    public VerifyPaymentRequest() {
    }

    public VerifyPaymentRequest(UUID operationId, String razorpayOrderId, String razorpayPaymentId, String razorpaySignature) {
        this.operationId = operationId;
        this.razorpayOrderId = razorpayOrderId;
        this.razorpayPaymentId = razorpayPaymentId;
        this.razorpaySignature = razorpaySignature;
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

    public String getRazorpaySignature() {
        return razorpaySignature;
    }

    public void setRazorpaySignature(String razorpaySignature) {
        this.razorpaySignature = razorpaySignature;
    }
}

