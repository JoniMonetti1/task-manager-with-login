package com.example.taskManagerWithLogin.repositories;

import com.example.taskManagerWithLogin.models.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    List<User> findAll();
    Optional<User> findById(Long id);
    Optional<User> findByUsername(String username);
    Optional<User> create(User user);
    Optional<User> update(Long id, User user);
    void delete(Long id);
}
