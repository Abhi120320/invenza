package com.invenza.entity;

import jakarta.persistence.*;
import java.util.UUID;

/**
 * Domain entity representing a user in the system.
 * Demonstrates OOP Encapsulation and Domain Business Methods.
 */
@Entity
@Table(name = "users", indexes = {
    @Index(name = "ix_users_login_id", columnList = "login_id", unique = true),
    @Index(name = "ix_users_email", columnList = "email", unique = true)
})
public class User extends BaseEntity {

    @Column(name = "login_id", length = 12, nullable = false, unique = true)
    private String loginId;

    @Column(name = "email", length = 255, nullable = false, unique = true)
    private String email;

    @Column(name = "password_hash", length = 255, nullable = false)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private UserRole role = UserRole.WAREHOUSE_STAFF;

    public User() {
    }

    public User(UUID id) {
        super(id);
    }

    public User(String loginId, String email, String passwordHash, UserRole role) {
        this.loginId = loginId;
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = role != null ? role : UserRole.WAREHOUSE_STAFF;
    }

    public boolean isManager() {
        return this.role == UserRole.INVENTORY_MANAGER;
    }

    public void updatePassword(String newPasswordHash) {
        if (newPasswordHash == null || newPasswordHash.isBlank()) {
            throw new IllegalArgumentException("Password hash cannot be empty");
        }
        this.passwordHash = newPasswordHash;
    }

    public String getLoginId() {
        return loginId;
    }

    public void setLoginId(String loginId) {
        this.loginId = loginId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }
}

