package app.nss.webchat.controller;

import app.nss.webchat.dto.request.ChatRoomRequest;
import app.nss.webchat.entity.ChatRoom;
import app.nss.webchat.exception.ApplicationException;
import app.nss.webchat.service.ChatRoomService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/chat-rooms")
public class ChatRoomController {

    private final static Logger LOG = LoggerFactory.getLogger(ChatRoomController.class);

    private final ChatRoomService chatRoomService;

    @Autowired
    public ChatRoomController(ChatRoomService chatRoomService) {
        this.chatRoomService = chatRoomService;
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<ChatRoom>> getChatRooms() {
        return ResponseEntity.ok(chatRoomService.getChatRooms());
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(path = "/{id}")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<ChatRoom> getChatRoomById(@PathVariable(value = "id") Long id) {
        if (id <= 0) {
            throw new ApplicationException(HttpStatus.NOT_FOUND, "ChatRoom id must be specified.");
        }
        return ResponseEntity.ok(chatRoomService.getChatRoomById(id));
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(path = "/name/{name}")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<ChatRoom> getChatRoomByName(@PathVariable(value = "name") String name) {
        if (name == null) {
            throw new ApplicationException(HttpStatus.NOT_FOUND, "ChatRoom name must be specified.");
        }
        return ResponseEntity.ok(chatRoomService.getChatRoomByName(name));
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(path = "/user/{username}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ChatRoom> getChatRoomByUser(@PathVariable(value = "username") String username) {
        if (username.isEmpty()) {
            throw new ApplicationException(HttpStatus.NOT_FOUND, "User name must be specified.");
        }
        return ResponseEntity.ok(chatRoomService.getChatRoomByUser(username));
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(path = "/")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<ChatRoom>> getChatRoomBySpecificSort(@RequestParam(value = "filter") String filter,
                                                                @RequestBody ChatRoomRequest request) {
        if (filter.equals("message")) {
            if (request.message().isEmpty()) {
                throw new ApplicationException(
                        HttpStatus.NOT_FOUND, "Message " + request.message() + " not found.");
            }
            return ResponseEntity.ok(chatRoomService.getChatRoomsBySpecificMessage(request.message()));
        } else if (filter.equals("participant")) {
            if (request.participant().isEmpty()) {
                  throw new ApplicationException(
                        HttpStatus.NOT_FOUND, "Participant " + request.participant() + " not found.");
            }
            return ResponseEntity.ok(chatRoomService.getChatRoomsBySpecificParticipant(request.participant()));
        } else {
            throw new ApplicationException(
                    HttpStatus.NOT_FOUND, "Specific sort must be specified: message/participant.");
        }
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<ChatRoom> createChatRoom(@RequestBody ChatRoomRequest chatRoomRequest) {
        if (chatRoomRequest.name().isEmpty() || chatRoomRequest.description().isEmpty()) {
            throw new ApplicationException(HttpStatus.NOT_FOUND, "ChatRoom name must be specified.");
        }
        LOG.debug("Chat room {} has been successfully created.", chatRoomRequest.name());
        return ResponseEntity.ok(
                chatRoomService.createChatRoom(chatRoomRequest.name(), chatRoomRequest.description())
        );
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(path = "/{id}/participants")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    public void addParticipant(@PathVariable long id, @RequestBody ChatRoomRequest request) {
        if (id <= 0) {
            throw new ApplicationException(HttpStatus.NOT_FOUND, "ChatRoom id must be specified.");
        }
        if (request.userId() <= 0) {
            throw new ApplicationException(HttpStatus.NOT_FOUND, "User id must be specified.");
        }
        LOG.debug("Participant {} has been successfully added to chat room {}.", request.userId(), id);
        chatRoomService.addParticipant(id, request.userId());
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping(path = "/{id}")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    public void updateChatRoom(
            @PathVariable(value = "id") Long id,
            @RequestBody ChatRoomRequest chatRoomRequest) {
        if (id <= 0) {
            throw new ApplicationException(HttpStatus.NOT_FOUND, "ChatRoom id must be specified.");
        }
        if (chatRoomRequest.name().isEmpty() || chatRoomRequest.description().isEmpty()) {
            throw new ApplicationException(HttpStatus.NOT_FOUND, "ChatRoom name must be specified.");
        }
        LOG.debug("Chat room {} has been successfully updated.", chatRoomRequest.name());
        chatRoomService.updateChatRoom(id, chatRoomRequest.name(), chatRoomRequest.description());
    }

    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping(path = "/{id}")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    public void deleteChatRoom(@PathVariable(value = "id") Long id) {
        if (id <= 0) {
            throw new ApplicationException(HttpStatus.NOT_FOUND, "ChatRoom id must be specified.");
        }
        LOG.debug("Chat room has been successfully deleted.");
        chatRoomService.deleteChatRoom(id);
    }

    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping(path = "/{id}/participants/{userId}")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    public void deleteParticipant(@PathVariable(value = "id") Long id, @PathVariable(value = "userId") Long userId) {
        if (id <= 0) {
            throw new ApplicationException(HttpStatus.NOT_FOUND, "ChatRoom id must be specified.");
        }
        if (userId <= 0) {
            throw new ApplicationException(HttpStatus.NOT_FOUND, "User id must be specified.");
        }
        LOG.debug("Participant has been successfully deleted from the chat room.");
        chatRoomService.deleteParticipant(id, userId);
    }
}
