package com.example.webchat.dto.request;

public record UserRequest(
        String username,
        String password,
        String email,
        String firstName,
        String lastName,
        String avatar
) {
}
