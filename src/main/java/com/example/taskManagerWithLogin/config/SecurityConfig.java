package com.example.taskManagerWithLogin.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig { //TODO: Update security config for register and create endpoints
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize.anyRequest().permitAll()) // Allow all requests
                .csrf(csrf -> csrf.disable()) // Disable CSRF protection
                .httpBasic(Customizer.withDefaults()); // Uses Customizer API for defaults (can be removed if basic authentication is not needed)

        return http.build();
    }
}
