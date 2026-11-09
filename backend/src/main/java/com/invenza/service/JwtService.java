package com.invenza.service;

import io.jsonwebtoken.Claims;
import java.util.UUID;

public interface JwtService {
    String createAccessToken(UUID userId);
    String createRefreshToken(UUID userId);
    Claims decodeToken(String token);
    UUID getUserIdFromToken(String token);
    String getTokenType(String token);
    boolean isTokenValid(String token, String expectedType);
}

