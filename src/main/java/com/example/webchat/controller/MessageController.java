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
    public ResponseEntity<Message> getMessageById(@PathVariable Long id) {
        if (id <= 0) {
            throw new ApplicationException(HttpStatus.NOT_FOUND, "Message id must be specified.");
        }
        return ResponseEntity.ok(messageService.getMessageById(id));
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(path = "/user/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<Message>> getSortedMessagesForUser(@RequestAttribute(value = "sort") String sort,
                                                                  @PathVariable Long id) {
        if (id <= 0) {
            throw new ApplicationException(HttpStatus.NOT_FOUND, "User id must be specified.");
        }
        if (sort.equals("asc")) {
            return ResponseEntity.ok(messageService.getSortedMessagesForUserAsc(id));
        } else if (sort.equals("desc")) {
            return ResponseEntity.ok(messageService.getSortedMessagesForUserDesc(id));
        } else {
            throw new ApplicationException(HttpStatus.NOT_FOUND, "Sort type must be specified (asc/desc).");
        }
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(path = "/chat/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<Message>> getSortedMessagesForChat(@PathVariable Long id) {
        if (id <= 0) {
            throw new ApplicationException(HttpStatus.NOT_FOUND, "Chat id must be specified.");
        }
        return ResponseEntity.ok(messageService.getSortedMessagesForChat(id));
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(path = "/sort/timestamp")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<Message>> getSortedMessagesByTimeStamp(
            @RequestParam(value = "timestamp") LocalDateTime timestamp) {
        if (timestamp == null) {
            throw new ApplicationException(HttpStatus.NOT_FOUND, "Timestamp must be specified.");
        }
        return ResponseEntity.ok(messageService.getSortedMessagesByTimeStamp(timestamp));
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(path = "/sort/keyword")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<Message>> getSortedMessagesByKeyword(@RequestParam String keyword) {
        if (keyword == null) {
            throw new ApplicationException(HttpStatus.NOT_FOUND, "Keyword must be specified.");
        }
        return ResponseEntity.ok(messageService.getSortedMessagesByKeyword(keyword));
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(path = "/sort/")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<Message>> getSortedMessages(@RequestParam(value = "limit") int limit,
                                                     @RequestParam(value = "offset") int offset) {
        if (limit <= 0) {
            throw new ApplicationException(HttpStatus.NOT_FOUND, "Limit must be specified.");
        }
        if (offset <= 0) {
            throw new ApplicationException(HttpStatus.NOT_FOUND, "Offset must be specified.");
        }
        return ResponseEntity.ok(messageService.getSortedMessages(limit, offset));
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<Message> createMessage(@RequestBody MessageRequest messageRequest) {
        if (messageRequest.content() == null) {
            throw new ApplicationException(HttpStatus.NOT_FOUND, "Message content must be specified.");
        }
        LOG.debug("Message has been successfully created.");
        return ResponseEntity.ok(messageService.createMessage(messageRequest.content()));
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping(path = "/{id}")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    public void updateMessage(@PathVariable Long id, @RequestBody MessageRequest messageRequest) {
        if (id <= 0) {
            throw new ApplicationException(HttpStatus.NOT_FOUND, "Message id must be specified.");
        }
        if (messageRequest.content() == null) {
            throw new ApplicationException(HttpStatus.NOT_FOUND, "Message content must be specified.");
        }
        LOG.debug("Message been successfully updated.");
        messageService.updateMessage(messageRequest.id(), messageRequest.content());
    }

    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping(path = "/{id}")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    public void deleteMessage(@PathVariable Long id) {
        if (id <= 0) {
            throw new ApplicationException(HttpStatus.NOT_FOUND, "Message id must be specified.");
        }
        LOG.debug("Message has been successfully deleted.");
        messageService.deleteMessage(id);
    }
}
