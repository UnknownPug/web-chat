package com.example.webchat.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

public record NotificationRequest(
        Long id,
        Long recipientId,
        String content,
        @JsonProperty("time_stamp")
        LocalDateTime timeStamp,
        Boolean status) {
}
