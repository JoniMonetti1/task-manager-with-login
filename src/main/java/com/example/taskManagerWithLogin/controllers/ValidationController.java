package com.example.taskManagerWithLogin.controllers;

import com.example.taskManagerWithLogin.models.dto.TokenValidationRequest;
import com.example.taskManagerWithLogin.services.AuthenticationService;
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

    public ValidationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/validate")
    public ResponseEntity<Map<String, Object>> validateToken(@RequestBody TokenValidationRequest request) {
        Map<String, Object> validationResult = authenticationService.validateJwtToken(request.getToken());

        return validationResult.get("isValid").equals(true)
                ? ResponseEntity.ok(validationResult)
                : ResponseEntity.badRequest().body(validationResult);
    }
}
