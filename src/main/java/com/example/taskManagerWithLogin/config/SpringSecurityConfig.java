package com.example.taskManagerWithLogin.config;

import com.example.taskManagerWithLogin.config.filter.JwtAuthenticationFilter;
import com.example.taskManagerWithLogin.config.filter.JwtValidationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
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
        return http.authorizeHttpRequests(authz -> authz
                        .requestMatchers(HttpMethod.GET, "/system/api/v1/users", "/system/api/v1/users/{id}", "/system/api/v1/users/username/").hasAnyRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/system/api/v1/users").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/system/api/v1/users/register").hasAnyRole("ADMIN", "USER")
                        .requestMatchers(HttpMethod.PUT, "/system/api/v1/users/{id}").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/system/api/v1/users/{id}").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/system/api/v1/users/{id}/tasks").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/system/api/v1/users/{id}/tasks/{taskId}", "/system/api/v1/users/{id}/tasks/filter").hasAnyRole("ADMIN", "USER")
                        .requestMatchers(HttpMethod.POST, "/system/api/v1/users/{id}/tasks").hasAnyRole("ADMIN", "USER")
                        .requestMatchers(HttpMethod.PUT, "/system/api/v1/users/{id}/tasks/{taskId}").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/system/api/v1/users/{id}/tasks/{taskId}").hasRole("ADMIN")
                        .anyRequest().authenticated())
                .addFilter(new JwtAuthenticationFilter(authenticationManager())) // Add JWT authentication filter
                .addFilter(new JwtValidationFilter(authenticationManager())) // Add JWT validation filter
                .csrf(AbstractHttpConfigurer::disable) // Disable CSRF protection
                .sessionManagement(management -> management.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .build();
    }
}
