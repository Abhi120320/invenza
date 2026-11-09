package com.invenza.controller;

import com.invenza.dto.auth.*;
import com.invenza.entity.User;
import com.invenza.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    private String getClientIp(HttpServletRequest request) {
        String xf = request.getHeader("X-Forwarded-For");
        if (xf != null && !xf.isBlank()) {
            return xf.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    @PostMapping("/signup/otp")
    public ResponseEntity<Map<String, String>> requestSignupOtp(@Valid @RequestBody OtpRequest request,
                                                               HttpServletRequest httpRequest) {
        Map<String, String> result = authService.requestSignupOtp(request.getEmail(), getClientIp(httpRequest));
        return ResponseEntity.ok(result);
    }

    @PostMapping("/signup")
    public ResponseEntity<UserResponse> signup(@Valid @RequestBody SignupRequest request) {
        UserResponse user = authService.signup(request);
        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest request,
                                              HttpServletRequest httpRequest) {
        TokenResponse tokens = authService.login(request, getClientIp(httpRequest));
        return ResponseEntity.ok(tokens);
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refresh(@Valid @RequestBody RefreshRequest request) {
        TokenResponse tokens = authService.refreshToken(request.getRefreshToken());
        return ResponseEntity.ok(tokens);
    }

    @PostMapping("/otp/request")
    public ResponseEntity<Map<String, String>> requestPasswordResetOtp(@Valid @RequestBody OtpRequest request,
                                                                      HttpServletRequest httpRequest) {
        Map<String, String> result = authService.requestPasswordResetOtp(request.getEmail(), getClientIp(httpRequest));
        return ResponseEntity.ok(result);
    }

    @PostMapping("/otp/verify")
    public ResponseEntity<Map<String, Object>> verifyOtp(@Valid @RequestBody OtpVerifyRequest request) {
        Map<String, Object> result = authService.verifyOtp(request.getEmail(), request.getCode());
        return ResponseEntity.ok(result);
    }

    @PostMapping("/password/reset")
    public ResponseEntity<Map<String, String>> resetPassword(@Valid @RequestBody PasswordResetRequest request) {
        Map<String, String> result = authService.resetPassword(request);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getMe(@AuthenticationPrincipal User currentUser) {
        UserResponse response = authService.getCurrentUserResponse(currentUser);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/password/change")
    public ResponseEntity<Map<String, String>> changePassword(@Valid @RequestBody PasswordChangeRequest request,
                                                              @AuthenticationPrincipal User currentUser) {
        Map<String, String> result = authService.changePassword(currentUser, request);
        return ResponseEntity.ok(result);
    }
}

