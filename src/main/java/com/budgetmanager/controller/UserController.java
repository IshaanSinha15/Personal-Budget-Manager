package com.budgetmanager.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.budgetmanager.controller.adapter.UserRequestAdapter;
import com.budgetmanager.controller.dto.LoginRequest;
import com.budgetmanager.controller.dto.RegisterRequest;
import com.budgetmanager.model.User;
import com.budgetmanager.service.UserService;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRequestAdapter userRequestAdapter;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        if (request == null ||
            request.getName() == null || request.getName().trim().isEmpty() ||
            request.getEmail() == null || request.getEmail().trim().isEmpty() ||
            request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "Name, email and password are required");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }

        User user = userRequestAdapter.toUser(request);

        try {
            User savedUser = userService.registerUser(user);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "User registered successfully");
            response.put("id", savedUser.getId());
            response.put("name", savedUser.getName());
            response.put("email", savedUser.getEmail());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException ex) {
            Map<String, String> error = new HashMap<>();
            error.put("message", ex.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        if (request == null ||
            request.getEmail() == null || request.getEmail().trim().isEmpty() ||
            request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "Email and password are required");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }

        User loggedInUser = userService.login(request.getEmail().trim(), request.getPassword());

        if (loggedInUser != null) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Login successful");
            response.put("id", loggedInUser.getId());
            response.put("name", loggedInUser.getName());
            response.put("email", loggedInUser.getEmail());
            return ResponseEntity.ok(response);
        }

        Map<String, String> error = new HashMap<>();
        error.put("message", "Invalid email or password");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

}