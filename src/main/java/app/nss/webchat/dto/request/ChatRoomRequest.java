package app.nss.webchat.dto.request;

public record ChatRoomRequest(

        Long id,
        String message,
        String participant,
        String name,
        String description,
        Long userId
) {}