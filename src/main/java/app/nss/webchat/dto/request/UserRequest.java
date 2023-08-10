package app.nss.webchat.dto.request;

import app.nss.webchat.entity.UserStatus;

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
