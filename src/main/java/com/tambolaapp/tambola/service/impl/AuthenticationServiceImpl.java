package com.tambolaapp.tambola.service.impl;

import com.tambolaapp.tambola.dto.JWTAuthenticationResponse;
import com.tambolaapp.tambola.dto.LoginRequest;
import com.tambolaapp.tambola.repository.UsersRepository;
import com.tambolaapp.tambola.service.AuthenticationService;
import com.tambolaapp.tambola.service.JWTService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
@RequiredArgsConstructor
@Log4j2
public class AuthenticationServiceImpl implements AuthenticationService {
    @Autowired
    private final UsersRepository userRepository;

    @Autowired
    private final JWTService jwtService;


    public JWTAuthenticationResponse signIn(LoginRequest signInRequest) {
        log.info("in signin "+signInRequest.getUsername()+"  "+signInRequest.getPassword());
        var user1 = userRepository.findByEmail(signInRequest.getUsername());
        if(user1.isEmpty()){
            return null;
        }
        if(!passwordCheck(signInRequest.getPassword(), user1.get().getPassword())){
            log.info("user is null in signIn AuthenticationServiceImpl");
            return null;
        }

        var user = userRepository.findByEmail(signInRequest.getUsername()).orElseThrow(() -> new IllegalArgumentException("Invalid Username or Password!"));
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername(user.getEmail());
        loginRequest.setPassword("DontBeOverSmartKid");
        var jwt = jwtService.generateToken(loginRequest);
        var refreshToken = jwtService.generateRefreshToken(new HashMap<>(), loginRequest);
        JWTAuthenticationResponse jwtAuthenticationResponse = new JWTAuthenticationResponse();
        jwtAuthenticationResponse.setToken(jwt);
        jwtAuthenticationResponse.setRefreshToken(refreshToken);
        return jwtAuthenticationResponse;
    }

    public boolean passwordCheck(String password, String passworddb){
        log.info(password,"   kk    ", passworddb);
        return password.equals(passworddb);
    }
}
