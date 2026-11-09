package com.invenza.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

/**
 * Domain entity representing an OTP verification code.
 * Encapsulates code validity rules and state transitions.
 */
@Entity
@Table(name = "otp_codes", indexes = {
    @Index(name = "ix_otp_codes_user_id", columnList = "user_id")
})
public class OtpCode extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "code", length = 6, nullable = false)
    private String code;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(name = "used", nullable = false)
    private boolean used = false;

    public OtpCode() {
    }

    public OtpCode(UUID id) {
        super(id);
    }

    public OtpCode(User user, String code, Instant expiresAt) {
        this.user = user;
        this.code = code;
        this.expiresAt = expiresAt;
        this.used = false;
    }

    public boolean isValid() {
        return !used && expiresAt != null && expiresAt.isAfter(Instant.now());
    }

    public void markUsed() {
        this.used = true;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(Instant expiresAt) {
        this.expiresAt = expiresAt;
    }

    public boolean isUsed() {
        return used;
    }

    public void setUsed(boolean used) {
        this.used = used;
    }
}

