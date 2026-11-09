package com.invenza.dto.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.invenza.entity.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class SignupRequest {

    @NotBlank(message = "Login ID is required")
    @Pattern(regexp = "^[a-zA-Z0-9_]{6,12}$", message = "Login ID must be 6–12 alphanumeric characters or underscores")
    @JsonProperty("login_id")
    private String loginId;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email address")
    private String email;

    @NotBlank(message = "Password is required")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[^a-zA-Z0-9]).{8,}$",
            message = "Password must be at least 8 characters with 1 uppercase, 1 lowercase, and 1 special character")
    private String password;

    @NotBlank(message = "Password confirmation is required")
    @JsonProperty("re_password")
    private String rePassword;

    @JsonProperty("otp_code")
    private String otpCode;

    private UserRole role = UserRole.WAREHOUSE_STAFF;

    public SignupRequest() {
    }

    public boolean doPasswordsMatch() {
        return password != null && password.equals(rePassword);
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRePassword() {
        return rePassword;
    }

    public void setRePassword(String rePassword) {
        this.rePassword = rePassword;
    }

    public String getOtpCode() {
        return otpCode;
    }

    public void setOtpCode(String otpCode) {
        this.otpCode = otpCode;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role != null ? role : UserRole.WAREHOUSE_STAFF;
    }
}

