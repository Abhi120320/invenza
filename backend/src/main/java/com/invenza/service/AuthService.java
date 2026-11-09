package com.invenza.service;

import com.invenza.dto.auth.*;
import com.invenza.entity.User;

import java.util.Map;

public interface AuthService {
    Map<String, String> requestSignupOtp(String email, String ip);
    UserResponse signup(SignupRequest request);
    TokenResponse login(LoginRequest request, String ip);
    TokenResponse refreshToken(String refreshToken);
    Map<String, String> requestPasswordResetOtp(String email, String ip);
    Map<String, Object> verifyOtp(String email, String code);
    Map<String, String> resetPassword(PasswordResetRequest request);
    Map<String, String> changePassword(User user, PasswordChangeRequest request);
    UserResponse getCurrentUserResponse(User user);
}

