package com.invenza.gateway;

/**
 * Strategy interface for payment gateway integration.
 * Demonstrates OOP Strategy Pattern and Dependency Inversion Principle.
 */
public interface PaymentGateway {

    /**
     * Create an order on the payment gateway.
     *
     * @param amountInPaise Amount in smallest currency unit (paise for INR)
     * @param currency      ISO currency code, e.g. "INR"
     * @param receipt       Receipt identifier
     * @param operationRef  Reference of the inventory operation
     * @return Generated order ID
     */
    String createOrder(long amountInPaise, String currency, String receipt, String operationRef);

    /**
     * Verify the cryptographic signature returned after payment completion.
     *
     * @param orderId   The order ID
     * @param paymentId The payment ID
     * @param signature The HMAC signature from the gateway
     * @return true if valid, false otherwise
     */
    boolean verifySignature(String orderId, String paymentId, String signature);

    /**
     * Get the public API key ID used by client checkout.
     */
    String getKeyId();
}

