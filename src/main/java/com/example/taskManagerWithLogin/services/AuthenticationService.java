package com.example.taskManagerWithLogin.services;

import java.util.Map;

public interface AuthenticationService {
    Map<String, Object> validateJwtToken(String token);
}
