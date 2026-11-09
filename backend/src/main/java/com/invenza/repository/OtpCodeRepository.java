package com.invenza.repository;

import com.invenza.entity.OtpCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OtpCodeRepository extends JpaRepository<OtpCode, UUID> {
    List<OtpCode> findByUserIdAndUsedFalse(UUID userId);
    Optional<OtpCode> findFirstByUserIdAndCodeAndUsedFalseAndExpiresAtAfterOrderByCreatedAtDesc(UUID userId, String code, Instant now);
}

