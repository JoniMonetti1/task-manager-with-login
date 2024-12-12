package com.example.taskManagerWithLogin.controllers;

import com.example.taskManagerWithLogin.exceptions.TaskNotFoundException;
import com.example.taskManagerWithLogin.models.Task;
import com.example.taskManagerWithLogin.models.User;
import com.example.taskManagerWithLogin.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    @CrossOrigin
    public ResponseEntity<List<User>> findAll() {
        List<User> users = userService.findAll();
        if (users.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    @CrossOrigin
    public ResponseEntity<User> findById(@PathVariable Long id) {
        return userService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/username/{username}")
    @CrossOrigin
    public ResponseEntity<User> findByUsername(@PathVariable String username) {
        return userService.findByUsername(username)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    @CrossOrigin
    public ResponseEntity<User> create(@RequestBody User user) {
        return userService.create(user)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    @CrossOrigin
    public ResponseEntity<User> update(@PathVariable Long id, @RequestBody User user) {
        return userService.update(id, user)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @CrossOrigin
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
    public ResponseEntity<List<Task>> findAllTasksByUser(@PathVariable Long id) {
        List<Task> tasks = userService.findAllTasksByUser(id);
        if (tasks.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/{id}/tasks/{taskId}")
    @CrossOrigin
    public ResponseEntity<Task> findTaskByUserAndTaskId(@PathVariable Long id, @PathVariable Long taskId) {
        return userService.findTaskByUserAndTaskId(id, taskId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/tasks")
    @CrossOrigin
    public ResponseEntity<Task> createTaskByUser(@PathVariable Long id, @RequestBody Task task) {
        return userService.createTaskByUser(id, task)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }


    @PutMapping("/{id}/tasks/{taskId}")
    @CrossOrigin
    public ResponseEntity<Task> updateTaskByUser(@PathVariable Long id, @PathVariable Long taskId, @RequestBody Task task) {
        return userService.updateTaskByUser(id, taskId, task)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }


    @DeleteMapping("/{id}/tasks/{taskId}")
    @CrossOrigin
    public ResponseEntity<?> deleteTaskByUser(@PathVariable Long id, @PathVariable Long taskId) {
        try {
            userService.deleteTaskByUser(id, taskId);
            return ResponseEntity.noContent().build();
        } catch (TaskNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Task not found for the given user"));
        }
    }
}
