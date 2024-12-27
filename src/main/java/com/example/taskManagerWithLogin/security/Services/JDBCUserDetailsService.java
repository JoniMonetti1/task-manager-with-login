package com.example.taskManagerWithLogin.security.Services;

import com.example.taskManagerWithLogin.models.User;
import com.example.taskManagerWithLogin.repositories.UserRepository;
import com.example.taskManagerWithLogin.security.CustomUserDetails;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

@Service
public class JDBCUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public JDBCUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .map(this::mapToUserDetails)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
    }

    private CustomUserDetails mapToUserDetails(User user) {
        return new CustomUserDetails(
                user.getUsername(),
                user.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority(user.getRole().name())),
                user.getId()
        );
    }
}
