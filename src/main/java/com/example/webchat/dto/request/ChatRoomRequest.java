package com.example.webchat.dto.request;

import com.example.webchat.entity.Message;
import com.example.webchat.entity.User;

import java.util.List;

public record ChatRoomRequest(

        Long id,
        String name,
        String description,

        List<Message> message,

        List<User> participants,

        Long userId
) {
}