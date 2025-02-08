package com.example.taskManagerWithLogin.controllers;

import com.example.taskManagerWithLogin.models.dto.TokenValidationRequest;
import com.example.taskManagerWithLogin.security.JwtUtils;
import com.example.taskManagerWithLogin.services.AuthenticationService;
import io.jsonwebtoken.Claims;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class ValidationController {

    private final AuthenticationService authenticationService;
    private final JwtUtils jwtUtils;

    public ValidationController(AuthenticationService authenticationService, JwtUtils jwtUtils) {
        this.authenticationService = authenticationService;
        this.jwtUtils = jwtUtils;
    }

    @PostMapping("/validate")
    public ResponseEntity<Map<String, Object>> validateToken(@RequestBody TokenValidationRequest request) {
        String token = request.getToken();

        try {
            Claims claims = jwtUtils.validateToken(token);
            if (claims.get("userId") == null || claims.getSubject() == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
}
