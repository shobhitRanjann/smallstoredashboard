package com.tambolaapp.tambola.service.impl;

import com.tambolaapp.tambola.dto.LoginRequest;
import com.tambolaapp.tambola.entity.User;
import com.tambolaapp.tambola.repository.UsersRepository;
import com.tambolaapp.tambola.service.JWTService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.SerialException;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import io.jsonwebtoken.Claims;

import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.function.Function;

@Service
@Log4j2
public class JWTServiceImpl implements JWTService {

    @Autowired
    UsersRepository usersRepository;


    public String extractUserName(String token){
        return extractClaim(token, Claims::getSubject);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolvers){
        final Claims claims = extratAllClaims(token);
        return claimsResolvers.apply(claims);
    }
    private Claims extratAllClaims(String token){
        return Jwts.parserBuilder().setSigningKey(getsignKey()).build().parseClaimsJws(token).getBody();
    }

    @Override
    public String generateToken(LoginRequest userDetails) {
        log.info("generate token  ::: ",getsignKey());
        User user;
        try{
            log.info("from JWTSERVICE IMPL"+ userDetails.getUsername());
            user = usersRepository.findByEmail(userDetails.getUsername()).orElseThrow();
            log.info("from jwtserviceImpl  > "+user);
        }catch (Exception e){
            return "error";
        }

        Claims claims = Jwts.claims().setSubject(userDetails.getUsername());
        claims.put("name", user.getEmail());
        return Jwts.builder().setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24))
                .signWith(getsignKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    @Override
    public boolean isTokenValid(String token, LoginRequest userDetails) {
        return false;
    }

    @Override
    public void validateToken(String token) {

    }

    @Override
    public String generateRefreshToken(HashMap<String, Object> extraClaims, LoginRequest userDetails) {
        return "";
    }

    private Key getsignKey(){
        log.info("genrating signing key");
        byte[] key = Decoders.BASE64.decode("eyJzdWIiOiJhZG1pbkBnbWFpbC5jb20iLCJpYXQiOjE2OTc5NTkyNDcsImV4cCI6MTY5Nzk2MDY4N30");
        log.info("generated signin key : "+ key);
        return Keys.hmacShaKeyFor(key);
    }
}
