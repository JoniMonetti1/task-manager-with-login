package com.example.taskManagerWithLogin.security;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

@Getter
public class CustomUserDetails extends User {
    private final long id;

    public CustomUserDetails(String username, String password, Collection<? extends GrantedAuthority> authorities, Long id) {
        super(username, password, true, true, true, true, authorities);
        this.id = id;
    }
}
