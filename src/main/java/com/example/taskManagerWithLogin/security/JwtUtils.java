package com.example.taskManagerWithLogin.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtils {

    private final SecretKey secretKey;
    private final long expirationMs = 3600000;

    public JwtUtils() {
        this.secretKey = TokenJwtConfig.SECRET_KEY;
    }

    public String generateToken(String username, String role, long id) {
        return Jwts.builder()
                .subject(username)
                .claim("role", role)
                .claim("userId", id)
                .expiration(new Date(System.currentTimeMillis() + expirationMs))
                .issuedAt(new Date())
                .signWith(secretKey)
                .compact();
    }

    public Claims validateToken(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

}


