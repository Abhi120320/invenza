package com.invenza.repository;

import com.invenza.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, UUID> {

    Optional<Payment> findByRazorpayOrderId(String razorpayOrderId);

    Optional<Payment> findTopByOperationIdOrderByCreatedAtDesc(UUID operationId);

    List<Payment> findByOperationIdOrderByCreatedAtDesc(UUID operationId);
}

