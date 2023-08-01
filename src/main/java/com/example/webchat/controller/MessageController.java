package com.example.webchat.controller;


import com.example.webchat.dto.request.MessageRequest;
import com.example.webchat.entity.Message;
import com.example.webchat.exception.ApplicationException;
import com.example.webchat.service.MessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping(path = "/message")
public class MessageController {

    private static final Logger LOG = LoggerFactory.getLogger(MessageController.class);

    private final MessageService messageService;

    @Autowired
    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<Message>> getAllMessages() {
        return ResponseEntity.ok(messageService.getAllMessages());
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(path = "/{id}")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<Message> getMessageById(@PathVariable(value = "id") Long id) {
        if (id <= 0) {
            throw new ApplicationException(HttpStatus.NOT_FOUND, "Message id must be specified.");
        }
        return ResponseEntity.ok(messageService.getMessageById(id));
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(path = "/filter/{id}")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<List<Message>> getFilteredMessages(
            @RequestParam(value = "type") String filter,
            @RequestParam(value = "sort", required = false) String sort,
            @PathVariable(value = "id") Long id
    ) {
        if (filter.equals("chat")) {
            if (id <= 0) {
                throw new ApplicationException(HttpStatus.NOT_FOUND, "Chat id must be specified.");
            }
            return ResponseEntity.ok(messageService.getSortedMessagesForChat(id));
        } else if (filter.equals("user")) {
            if (id <= 0) {
                throw new ApplicationException(HttpStatus.NOT_FOUND, "User id must be specified.");
            }
            if (sort.equals("asc")) {
                return ResponseEntity.ok(messageService.getSortedMessagesForUserAsc(id));
            } else if (sort.equals("desc")) {
                return ResponseEntity.ok(messageService.getSortedMessagesForUserDesc(id));
            } else {
                throw new ApplicationException(HttpStatus.NOT_FOUND, "Sort type must be specified: asc/desc.");
            }
        } else {
            throw new ApplicationException(HttpStatus.NOT_FOUND, "Type must be specified: chat/user.");
        }
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(path = "/sort")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    public ResponseEntity<List<Message>> getSortedMessages(
            @RequestParam(value = "limit", required = false) Integer limit,
            @RequestParam(value = "offset", required = false) Integer offset,
            @RequestParam(value = "timestamp", required = false) LocalDateTime timestamp,
            @RequestParam(value = "keyword", required = false) String keyword
    ) {
        if (limit != null && limit <= 0) {
            throw new ApplicationException(HttpStatus.NOT_FOUND, "Limit must be specified.");
        }
        if (offset != null && offset <= 0) {
            throw new ApplicationException(HttpStatus.NOT_FOUND, "Offset must be specified.");
        }
        if (timestamp != null) {
            return ResponseEntity.ok(messageService.getSortedMessagesByTimeStamp(timestamp));
        } else if (keyword != null) {
            return ResponseEntity.ok(messageService.getSortedMessagesByKeyword(keyword));
        } else if (limit != null && offset != null) {
            return ResponseEntity.ok(messageService.getSortedMessages(limit, offset));
        } else {
            throw new ApplicationException(
                    HttpStatus.NOT_FOUND, "Sort type must be specified: limit, offset/timestamp, keyword.");
        }
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<Message> sendMessage(@RequestBody MessageRequest messageRequest) {
        if (messageRequest.content() == null) {
            throw new ApplicationException(HttpStatus.NOT_FOUND, "Message content must be specified.");
        }
        LOG.debug("Message has been successfully created.");
        return ResponseEntity.ok(messageService.sendMessage(
                messageRequest.content(),
                messageRequest.roomId(),
                messageRequest.senderId()
        ));
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping(path = "/{id}")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    public void updateMessage(@PathVariable(value = "id") Long id, @RequestBody MessageRequest messageRequest) {
        if (id <= 0) {
            throw new ApplicationException(HttpStatus.NOT_FOUND, "Message id must be specified.");
        }
        if (messageRequest.content() == null) {
            throw new ApplicationException(HttpStatus.NOT_FOUND, "Message content must be specified.");
        }
        messageService.updateMessage(id, messageRequest.content());
        LOG.debug("Message been successfully updated.");
    }

    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping(path = "/{id}")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    public void deleteMessage(@PathVariable(value = "id") Long id) {
        if (id <= 0) {
            throw new ApplicationException(HttpStatus.NOT_FOUND, "Message id must be specified.");
        }
        LOG.debug("Message has been successfully deleted.");
        messageService.deleteMessage(id);
    }
}
