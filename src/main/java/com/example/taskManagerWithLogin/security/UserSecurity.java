package com.example.taskManagerWithLogin.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component("userSecurity")
@Slf4j
public class UserSecurity {
    public boolean isCurrentUser(Long userId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication.getPrincipal() == null) {
            return false;
        }

        if (authentication.getPrincipal() instanceof CustomUserDetails userDetails) {
            return userId.equals(userDetails.getId());
        }

        return false;
    }
}
