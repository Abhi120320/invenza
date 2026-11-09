package com.invenza;

import com.invenza.dto.auth.LoginRequest;
import com.invenza.dto.auth.SignupRequest;
import com.invenza.dto.auth.TokenResponse;
import com.invenza.dto.auth.UserResponse;
import com.invenza.entity.UserRole;
import com.invenza.exception.BadRequestException;
import com.invenza.exception.ConflictException;
import com.invenza.exception.UnauthorizedException;
import com.invenza.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class AuthServiceTest {

    @Autowired
    private AuthService authService;

    @Test
    void testSignupAndLoginFlow() {
        String email = "testuser@example.com";
        String ip = "127.0.0.1";

        // 1. Request signup OTP
        Map<String, String> otpResp = authService.requestSignupOtp(email, ip);
        assertNotNull(otpResp);
        String devCode = otpResp.get("dev_code");
        assertNotNull(devCode);

        // 2. Signup
        SignupRequest signupReq = new SignupRequest();
        signupReq.setLoginId("user123");
        signupReq.setEmail(email);
        signupReq.setPassword("Password123!");
        signupReq.setRePassword("Password123!");
        signupReq.setOtpCode(devCode);
        signupReq.setRole(UserRole.INVENTORY_MANAGER);

        UserResponse user = authService.signup(signupReq);
        assertNotNull(user);
        assertEquals("user123", user.getLoginId());
        assertEquals(email, user.getEmail());
        assertEquals(UserRole.INVENTORY_MANAGER, user.getRole());

        // 3. Login success
        LoginRequest loginReq = new LoginRequest("user123", "Password123!");
        TokenResponse tokens = authService.login(loginReq, ip);
        assertNotNull(tokens);
        assertNotNull(tokens.getAccessToken());
        assertNotNull(tokens.getRefreshToken());

        // 4. Login failure
        LoginRequest wrongLogin = new LoginRequest("user123", "WrongPass123!");
        assertThrows(UnauthorizedException.class, () -> authService.login(wrongLogin, ip));
    }

    @Test
    void testSignupPasswordsMismatch() {
        SignupRequest req = new SignupRequest();
        req.setLoginId("badmatch");
        req.setEmail("bad@example.com");
        req.setPassword("Password123!");
        req.setRePassword("Different123!");
        req.setOtpCode("123456");

        assertThrows(BadRequestException.class, () -> authService.signup(req));
    }
}

