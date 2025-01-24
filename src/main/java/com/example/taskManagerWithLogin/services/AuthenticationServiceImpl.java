package com.example.taskManagerWithLogin.services;

import com.example.taskManagerWithLogin.security.JwtUtils;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class AuthenticationServiceImpl implements AuthenticationService{

    private final JwtUtils jwtUtils;

    public AuthenticationServiceImpl(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    @Override
    public Map<String, Object> validateJwtToken(String token) {
        return jwtUtils.validateToken(token);
    }
}
