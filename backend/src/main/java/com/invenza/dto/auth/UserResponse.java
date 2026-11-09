package com.invenza.dto.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.invenza.entity.User;
import com.invenza.entity.UserRole;

import java.time.Instant;
import java.util.UUID;

public class UserResponse {

    private UUID id;

    @JsonProperty("login_id")
    private String loginId;

    private String email;

    private UserRole role;

    @JsonProperty("created_at")
    private Instant createdAt;

    public UserResponse() {
    }

    public UserResponse(UUID id, String loginId, String email, UserRole role, Instant createdAt) {
        this.id = id;
        this.loginId = loginId;
        this.email = email;
        this.role = role;
        this.createdAt = createdAt;
    }

    public static UserResponse fromEntity(User user) {
        if (user == null) return null;
        return new UserResponse(
                user.getId(),
                user.getLoginId(),
                user.getEmail(),
                user.getRole(),
                user.getCreatedAt()
        );
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
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

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}

