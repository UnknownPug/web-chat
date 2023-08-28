package app.nss.webchat.controller;


import app.nss.webchat.dto.request.MessageRequest;
import app.nss.webchat.entity.Message;
import app.nss.webchat.exception.ApplicationException;
import app.nss.webchat.service.MessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/message")
public class MessageController {

    private final MessageService messageService;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    public MessageController(MessageService messageService, KafkaTemplate<String, String> kafkaTemplate) {
        this.messageService = messageService;
        this.kafkaTemplate = kafkaTemplate;
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

    @GetMapping(path = "/sort")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    public ResponseEntity<List<Message>> getSortedMessages(
            @RequestParam(value = "limit", required = false) Integer limit,
            @RequestParam(value = "offset", required = false) Integer offset,
            @RequestParam(value = "keyword", required = false) String keyword
    ) {
        if ((limit != null && limit <= 0) || (offset != null && offset < 0)) {
            throw new ApplicationException(HttpStatus.NOT_FOUND, "Limit and offset must be specified.");
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
        if (!messageService.containsUserInRoom(messageRequest.roomId(), messageRequest.senderId())) {
            throw new ApplicationException(HttpStatus.NOT_FOUND, "Chat room does not contain this user.");
        }
        kafkaTemplate.send("messages", messageRequest.roomId().toString(), messageRequest.content());
        log.info("Message has been successfully sent.");
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
        if (messageRequest.senderId() <= 0) {
            throw new ApplicationException(HttpStatus.NOT_FOUND, "Sender id must be specified.");
        }
        if (messageRequest.content() == null) {
            throw new ApplicationException(HttpStatus.NOT_FOUND, "Message content must be specified.");
        }
        messageService.updateMessage(id, messageRequest.senderId(), messageRequest.content());
        kafkaTemplate.send("messages", id.toString(), messageRequest.content());
        log.info("Message been successfully updated.");
    }

    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping(path = "/{id}")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    public void deleteMessage(@PathVariable(value = "id") Long id) {
        if (id <= 0) {
            throw new ApplicationException(HttpStatus.NOT_FOUND, "Message id must be specified.");
        }
        messageService.deleteMessage(id);
        log.info("Message has been successfully deleted.");
        kafkaTemplate.send("messages", id.toString(), "");
    }
}
