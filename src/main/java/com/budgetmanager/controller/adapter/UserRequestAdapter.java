package com.budgetmanager.controller.adapter;

import org.springframework.stereotype.Component;

import com.budgetmanager.controller.dto.RegisterRequest;
import com.budgetmanager.model.User;

@Component
public class UserRequestAdapter {

    public User toUser(RegisterRequest request) {
        return User.builder()
                .name(request.getName().trim())
                .email(request.getEmail().trim())
                .password(request.getPassword())
                .build();
    }
}
