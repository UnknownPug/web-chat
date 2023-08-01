package com.example.webchat.dto.request;

import com.example.webchat.entity.UserStatus;

public record UserRequest(
        String username,
        String password,
        String email,
        String firstName,
        String lastName,
        String avatar,

        UserStatus status
) {
}
