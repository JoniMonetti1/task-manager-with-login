package com.example.taskManagerWithLogin.config;

import com.example.taskManagerWithLogin.config.filter.JwtAuthenticationFilter;
import com.example.taskManagerWithLogin.config.filter.JwtValidationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SpringSecurityConfig {

    @Autowired
    private AuthenticationConfiguration authenticationConfiguration;

    @Bean
    AuthenticationManager authenticationManager() throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize.anyRequest().permitAll()) // Allow all requests
                .addFilter(new JwtAuthenticationFilter(authenticationManager())) // Add JWT authentication filter
                .addFilter(new JwtValidationFilter(authenticationManager())) // Add JWT validation filter
                .csrf(csrf -> csrf.disable()) // Disable CSRF protection
                .httpBasic(Customizer.withDefaults()); // Uses Customizer API for defaults (can be removed if basic authentication is not needed)

        return http.build();
    }
}
