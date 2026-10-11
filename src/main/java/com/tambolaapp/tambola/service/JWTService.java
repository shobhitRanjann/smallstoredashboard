package com.tambolaapp.tambola.service;

import com.tambolaapp.tambola.dto.LoginRequest;

import java.util.HashMap;

public interface JWTService {

    String extractUserName(String token);

    String generateToken(LoginRequest userDetails);

    boolean isTokenValid(String token, LoginRequest userDetails);

    public void validateToken(String token);

    String generateRefreshToken(HashMap<String, Object> extraClaims, LoginRequest userDetails);
}
