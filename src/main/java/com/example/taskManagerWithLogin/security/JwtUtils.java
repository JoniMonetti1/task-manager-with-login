package com.example.taskManagerWithLogin.security;

import io.jsonwebtoken.Jwts;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtils {

    private final SecretKey secretKey;

    public JwtUtils() {
        this.secretKey = TokenJwtConfig.SECRET_KEY;
    }

    public Map<String, Object> validateToken(String token) {
        Map<String, Object> response = new HashMap<>();

        try {
            Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parse(token);

            String[] chunks = token.split("\\.");
            Base64.Decoder decoder = Base64.getDecoder();

            String header = new String(decoder.decode(chunks[0]));
            String payload = new String(decoder.decode(chunks[1]));

            response.put("isValid", true);
            response.put("header", header);
            response.put("payload", payload);

            return response;
        } catch (Exception e) {
            response.put("isValid", false);
            response.put("error", e.getMessage());
            return response;
        }
    }

}


