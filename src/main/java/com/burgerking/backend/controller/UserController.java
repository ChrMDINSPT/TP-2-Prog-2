package com.burgerking.backend.controller;

import com.burgerking.backend.dto.CreateUserRequest;
import com.burgerking.backend.entity.User;
import com.burgerking.backend.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(
            UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public User getUserById(
            @PathVariable Long id) {

        return userService.getUserById(id);
    }

    @PostMapping
    public User createUser(
            @RequestBody CreateUserRequest request) {

        return userService.createUser(request);
    }

    @PutMapping("/{id}")
    public User updateUser(
            @PathVariable Long id,
            @RequestBody CreateUserRequest request) {

        return userService.updateUser(
                id,
                request
        );
    }

    @DeleteMapping("/{id}")
    public void deleteUser(
            @PathVariable Long id) {

        userService.deleteUser(id);
    }
}