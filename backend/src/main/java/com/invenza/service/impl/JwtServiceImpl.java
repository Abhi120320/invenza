package com.invenza.service.impl;

import com.invenza.service.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;

@Service
public class JwtServiceImpl implements JwtService {

    private final SecretKey key;
    private final int accessTokenExpireMinutes;
    private final int refreshTokenExpireDays;

    public JwtServiceImpl(
            @Value("${app.jwt.secret:invenza-super-secret-jwt-signing-key-production-ready-256-bit}") String secret,
            @Value("${app.jwt.access-token-expire-minutes:30}") int accessTokenExpireMinutes,
            @Value("${app.jwt.refresh-token-expire-days:7}") int refreshTokenExpireDays) {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        if (keyBytes.length < 32) {
            byte[] padded = new byte[32];
            System.arraycopy(keyBytes, 0, padded, 0, keyBytes.length);
            this.key = Keys.hmacShaKeyFor(padded);
        } else {
            this.key = Keys.hmacShaKeyFor(keyBytes);
        }
        this.accessTokenExpireMinutes = accessTokenExpireMinutes;
        this.refreshTokenExpireDays = refreshTokenExpireDays;
    }

    @Override
    public String createAccessToken(UUID userId) {
        Instant now = Instant.now();
        Instant exp = now.plus(accessTokenExpireMinutes, ChronoUnit.MINUTES);
        return Jwts.builder()
                .subject(userId.toString())
                .claim("type", "access")
                .issuedAt(Date.from(now))
                .expiration(Date.from(exp))
                .signWith(key)
                .compact();
    }

    @Override
    public String createRefreshToken(UUID userId) {
        Instant now = Instant.now();
        Instant exp = now.plus(refreshTokenExpireDays, ChronoUnit.DAYS);
        return Jwts.builder()
                .subject(userId.toString())
                .claim("type", "refresh")
                .issuedAt(Date.from(now))
                .expiration(Date.from(exp))
                .signWith(key)
                .compact();
    }

    @Override
    public Claims decodeToken(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (JwtException | IllegalArgumentException e) {
            return null;
        }
    }

    @Override
    public UUID getUserIdFromToken(String token) {
        Claims claims = decodeToken(token);
        if (claims != null && claims.getSubject() != null) {
            try {
                return UUID.fromString(claims.getSubject());
            } catch (IllegalArgumentException e) {
                return null;
            }
        }
        return null;
    }

    @Override
    public String getTokenType(String token) {
        Claims claims = decodeToken(token);
        if (claims != null) {
            Object type = claims.get("type");
            return type != null ? type.toString() : null;
        }
        return null;
    }

    @Override
    public boolean isTokenValid(String token, String expectedType) {
        Claims claims = decodeToken(token);
        if (claims == null) return false;
        String type = claims.get("type", String.class);
        return expectedType.equalsIgnoreCase(type) && claims.getExpiration().after(new Date());
    }
}

