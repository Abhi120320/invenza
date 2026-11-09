package com.invenza.service.impl;

import com.invenza.dto.auth.*;
import com.invenza.entity.OtpCode;
import com.invenza.entity.User;
import com.invenza.exception.BadRequestException;
import com.invenza.exception.ConflictException;
import com.invenza.exception.UnauthorizedException;
import com.invenza.repository.OtpCodeRepository;
import com.invenza.repository.UserRepository;
import com.invenza.service.AuthService;
import com.invenza.service.EmailService;
import com.invenza.service.JwtService;
import com.invenza.service.RateLimiterService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final OtpCodeRepository otpCodeRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RateLimiterService rateLimiterService;
    private final EmailService emailService;

    // In-memory store for signup OTPs: email -> {code, expiresAt}
    private static class SignupOtpEntry {
        final String code;
        final Instant expiresAt;

        SignupOtpEntry(String code, Instant expiresAt) {
            this.code = code;
            this.expiresAt = expiresAt;
        }
    }

    private final Map<String, SignupOtpEntry> signupOtps = new ConcurrentHashMap<>();
    private final SecureRandom random = new SecureRandom();

    public AuthServiceImpl(UserRepository userRepository,
                           OtpCodeRepository otpCodeRepository,
                           PasswordEncoder passwordEncoder,
                           JwtService jwtService,
                           RateLimiterService rateLimiterService,
                           EmailService emailService) {
        this.userRepository = userRepository;
        this.otpCodeRepository = otpCodeRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.rateLimiterService = rateLimiterService;
        this.emailService = emailService;
    }

    private String generateOtp() {
        return String.format("%06d", random.nextInt(1000000));
    }

    @Override
    public Map<String, String> requestSignupOtp(String email, String ip) {
        String cleanEmail = email.trim().toLowerCase();
        rateLimiterService.checkRateLimit("signup_otp:" + cleanEmail, ip);

        if (userRepository.existsByEmail(cleanEmail)) {
            throw new ConflictException("Email is already registered");
        }

        String code = generateOtp();
        Instant expires = Instant.now().plus(10, ChronoUnit.MINUTES);
        signupOtps.put(cleanEmail, new SignupOtpEntry(code, expires));

        emailService.sendOtpEmail(cleanEmail, code);
        rateLimiterService.recordFailure("signup_otp:" + cleanEmail, ip);

        Map<String, String> response = new HashMap<>();
        response.put("message", "OTP sent to your email");
        if (emailService.isDevMode()) {
            response.put("dev_code", code);
        }
        return response;
    }

    @Override
    @Transactional
    public UserResponse signup(SignupRequest request) {
        String cleanEmail = request.getEmail().trim().toLowerCase();

        if (!request.doPasswordsMatch()) {
            throw new BadRequestException("Passwords do not match");
        }

        String otp = request.getOtpCode();
        if (otp == null || otp.isBlank()) {
            throw new BadRequestException("Email verification OTP is required");
        }

        SignupOtpEntry stored = signupOtps.get(cleanEmail);
        if (stored == null || !stored.code.equals(otp) || stored.expiresAt.isBefore(Instant.now())) {
            throw new BadRequestException("Invalid or expired OTP");
        }

        if (userRepository.existsByLoginId(request.getLoginId())) {
            throw new ConflictException("Login ID is already taken");
        }

        if (userRepository.existsByEmail(cleanEmail)) {
            throw new ConflictException("Email is already registered");
        }

        User user = new User(
                request.getLoginId(),
                cleanEmail,
                passwordEncoder.encode(request.getPassword()),
                request.getRole()
        );

        User savedUser = userRepository.save(user);
        signupOtps.remove(cleanEmail);

        return UserResponse.fromEntity(savedUser);
    }

    @Override
    public TokenResponse login(LoginRequest request, String ip) {
        rateLimiterService.checkRateLimit(request.getLoginId(), ip);

        User user = userRepository.findByLoginId(request.getLoginId()).orElse(null);

        if (user == null || !passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            int attempts = rateLimiterService.recordFailure(request.getLoginId(), ip);
            Integer remaining = rateLimiterService.getRemainingAttemptsBeforeLockout(attempts);

            String remainingMsg = remaining != null
                    ? String.format(" %d attempt(s) left before lockout.", remaining)
                    : "";

            throw new UnauthorizedException(
                    "Invalid Login Id or Password." + remainingMsg,
                    attempts
            );
        }

        rateLimiterService.recordSuccess(request.getLoginId(), ip);

        String access = jwtService.createAccessToken(user.getId());
        String refresh = jwtService.createRefreshToken(user.getId());

        return new TokenResponse(access, refresh);
    }

    @Override
    public TokenResponse refreshToken(String refreshToken) {
        if (!jwtService.isTokenValid(refreshToken, "refresh")) {
            throw new UnauthorizedException("Invalid refresh token");
        }

        UUID userId = jwtService.getUserIdFromToken(refreshToken);
        if (userId == null) {
            throw new UnauthorizedException("Invalid refresh token");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UnauthorizedException("User not found"));

        String access = jwtService.createAccessToken(user.getId());
        String refresh = jwtService.createRefreshToken(user.getId());

        return new TokenResponse(access, refresh);
    }

    @Override
    @Transactional
    public Map<String, String> requestPasswordResetOtp(String email, String ip) {
        String cleanEmail = email.trim().toLowerCase();
        rateLimiterService.checkRateLimit(cleanEmail, ip);

        User user = userRepository.findByEmail(cleanEmail).orElse(null);
        if (user == null) {
            // Constant response to prevent enumeration
            return Map.of("message", "If this email exists, an OTP has been sent");
        }

        // Invalidate unused OTPs
        List<OtpCode> existingOtps = otpCodeRepository.findByUserIdAndUsedFalse(user.getId());
        for (OtpCode oldOtp : existingOtps) {
            oldOtp.markUsed();
        }
        otpCodeRepository.saveAll(existingOtps);

        String code = generateOtp();
        Instant expires = Instant.now().plus(10, ChronoUnit.MINUTES);
        OtpCode otpCode = new OtpCode(user, code, expires);
        otpCodeRepository.save(otpCode);

        emailService.sendOtpEmail(cleanEmail, code);
        rateLimiterService.recordFailure(cleanEmail, ip);

        Map<String, String> response = new HashMap<>();
        response.put("message", "If this email exists, an OTP has been sent");
        if (emailService.isDevMode()) {
            response.put("dev_code", code);
        }
        return response;
    }

    @Override
    public Map<String, Object> verifyOtp(String email, String code) {
        String cleanEmail = email.trim().toLowerCase();
        User user = userRepository.findByEmail(cleanEmail)
                .orElseThrow(() -> new BadRequestException("Invalid OTP"));

        otpCodeRepository.findFirstByUserIdAndCodeAndUsedFalseAndExpiresAtAfterOrderByCreatedAtDesc(
                user.getId(), code, Instant.now()
        ).orElseThrow(() -> new BadRequestException("Invalid or expired OTP"));

        return Map.of("message", "OTP verified", "valid", true);
    }

    @Override
    @Transactional
    public Map<String, String> resetPassword(PasswordResetRequest request) {
        if (!request.doPasswordsMatch()) {
            throw new BadRequestException("Passwords do not match");
        }

        String cleanEmail = request.getEmail().trim().toLowerCase();
        User user = userRepository.findByEmail(cleanEmail)
                .orElseThrow(() -> new BadRequestException("Invalid request"));

        OtpCode otp = otpCodeRepository.findFirstByUserIdAndCodeAndUsedFalseAndExpiresAtAfterOrderByCreatedAtDesc(
                user.getId(), request.getCode(), Instant.now()
        ).orElseThrow(() -> new BadRequestException("Invalid or expired OTP"));

        user.updatePassword(passwordEncoder.encode(request.getNewPassword()));
        otp.markUsed();

        userRepository.save(user);
        otpCodeRepository.save(otp);

        return Map.of("message", "Password reset successfully");
    }

    @Override
    @Transactional
    public Map<String, String> changePassword(User user, PasswordChangeRequest request) {
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPasswordHash())) {
            throw new BadRequestException("Incorrect current password");
        }

        user.updatePassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        return Map.of("message", "Password changed successfully");
    }

    @Override
    public UserResponse getCurrentUserResponse(User user) {
        return UserResponse.fromEntity(user);
    }
}

