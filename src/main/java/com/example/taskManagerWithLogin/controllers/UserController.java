package com.example.taskManagerWithLogin.controllers;

import com.example.taskManagerWithLogin.exceptions.DuplicateUsernameException;
import com.example.taskManagerWithLogin.exceptions.TaskNotFoundException;
import com.example.taskManagerWithLogin.models.Task;
import com.example.taskManagerWithLogin.models.User;
import com.example.taskManagerWithLogin.models.dto.TaskDTO;
import com.example.taskManagerWithLogin.models.dto.UserDTO;
import com.example.taskManagerWithLogin.models.dto.UserRegisterDTO;
import com.example.taskManagerWithLogin.services.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
@EnableMethodSecurity
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    @CrossOrigin
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserDTO>> findAll() {
        List<UserDTO> users = userService.findAll();
        if (users.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    @CrossOrigin
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> findById(@PathVariable Long id) {
        return userService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/username/{username}")
    @CrossOrigin
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> findByUsername(@PathVariable String username) {
        return userService.findByUsername(username)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    @CrossOrigin
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> create(@Valid @RequestBody User user) {
        return userService.create(user)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/register")
    @CrossOrigin
    public ResponseEntity<?> register(@Valid @RequestBody UserRegisterDTO userRegisterDTO) {
        return userService.register(userRegisterDTO)
                .map(token -> ResponseEntity.ok(Collections.singletonMap("token", token)))
                .orElseGet(() -> ResponseEntity.badRequest()
                        .body(Collections.singletonMap("error", "Username or email already exists")));
    }

    @PutMapping("/{id}")
    @CrossOrigin
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> update(@PathVariable Long id, @Valid @RequestBody User user) throws DuplicateUsernameException {
        return userService.update(id, user)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @CrossOrigin
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        if (userService.findById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        } else {
            userService.delete(id);
            return ResponseEntity.noContent().build();
        }
    }

    //Tasks related endpoints

    @GetMapping("/{id}/tasks")
    @CrossOrigin
    @PreAuthorize("hasRole('ADMIN') or @userSecurity.isCurrentUser(#id)")
    public ResponseEntity<List<Task>> findAllTasksByUser(@PathVariable Long id) {

        List<Task> tasks = userService.findAllTasksByUser(id);
        if (tasks.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/{id}/tasks/{taskId}")
    @CrossOrigin
    @PreAuthorize("hasRole('ADMIN') or @userSecurity.isCurrentUser(#id)")
    public ResponseEntity<Task> findTaskByUserAndTaskId(@PathVariable Long id, @PathVariable Long taskId) {
        return userService.findTaskByUserAndTaskId(id, taskId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/tasks")
    @CrossOrigin
    @PreAuthorize("hasRole('ADMIN') or @userSecurity.isCurrentUser(#id)")
    public ResponseEntity<Task> createTaskByUser(@PathVariable Long id, @Valid @RequestBody TaskDTO taskDTO) {
        return userService.createTaskByUser(id, taskDTO)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }


    @PutMapping("/{id}/tasks/{taskId}")
    @CrossOrigin
    @PreAuthorize("hasRole('ADMIN') or @userSecurity.isCurrentUser(#id)")
    public ResponseEntity<Task> updateTaskByUser(@PathVariable Long id, @PathVariable Long taskId, @Valid @RequestBody Task task) {
        return userService.updateTaskByUser(id, taskId, task)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }


    @DeleteMapping("/{id}/tasks/{taskId}")
    @CrossOrigin
    @PreAuthorize("hasRole('ADMIN') or @userSecurity.isCurrentUser(#id)")
    public ResponseEntity<?> deleteTaskByUser(@PathVariable Long id, @PathVariable Long taskId) {
        try {
            userService.deleteTaskByUser(id, taskId);
            return ResponseEntity.noContent().build();
        } catch (TaskNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Task not found for the given user"));
        }
    }


    @GetMapping("/{id}/tasks/filter")
    @CrossOrigin
    @PreAuthorize("hasRole('ADMIN') or @userSecurity.isCurrentUser(#id)")
    public ResponseEntity<List<Task>> filterTasksbyStatus(@PathVariable Long id, @RequestParam(required = false) String status) {
        List<Task> tasks = userService.filterTasksbyStatus(id, status);
        if (tasks.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(tasks);
    }

    @PutMapping("/{id}/tasks/{taskId}/status")
    @CrossOrigin
    @PreAuthorize("hasRole('ADMIN') or @userSecurity.isCurrentUser(#id)")
    public ResponseEntity<?> updateTaskStatus(@PathVariable Long id, @PathVariable Long taskId, @RequestBody String status) {
        return userService.updateTaskStatus(id, taskId, status)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());

    }
}
