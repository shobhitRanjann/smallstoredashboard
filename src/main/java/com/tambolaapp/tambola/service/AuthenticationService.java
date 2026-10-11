package com.tambolaapp.tambola.service;

import com.tambolaapp.tambola.dto.JWTAuthenticationResponse;
import com.tambolaapp.tambola.dto.LoginRequest;

public interface AuthenticationService {
    JWTAuthenticationResponse signIn(LoginRequest signInRequest);
}
