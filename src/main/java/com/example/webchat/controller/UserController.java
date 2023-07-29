package com.example.webchat.controller;

import com.example.webchat.dto.request.UserRequest;
import com.example.webchat.entity.User;
import com.example.webchat.entity.UserStatus;
import com.example.webchat.exception.ApplicationException;
import com.example.webchat.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/users")
public class UserController {

    private static final Logger LOG = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Iterable<User>> getUsers() {
        return ResponseEntity.ok(userService.getUsers());
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(path = "/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        if (id <= 0) {
            throw new ApplicationException(HttpStatus.NOT_FOUND, "User id must be specified.");
        }
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(path = "/{username}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    public ResponseEntity<User> getUserByUsername(@PathVariable String username) {
        if (username == null) {
            throw new ApplicationException(HttpStatus.NOT_FOUND, "Username must be specified.");
        }
        return ResponseEntity.ok(userService.getUserByUsername(username));
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(path = "/{email}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<User> getUserByEmail(@PathVariable String email) {
        if (email == null) {
            throw new ApplicationException(HttpStatus.NOT_FOUND, "Email must be specified.");
        }
        return ResponseEntity.ok(userService.getUserByEmail(email));
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(path = "/search")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    public ResponseEntity<User> getUserByKeyword(@RequestParam(value = "keyword") String keyword) {
        if (keyword == null) {
            throw new ApplicationException(HttpStatus.NOT_FOUND, "Keyword must be specified.");
        }
        return ResponseEntity.ok(userService.getUserByKeyword(keyword));
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(path = "/{id}/chat-rooms")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Iterable<User>> getChatRoomsByUserId(@PathVariable Long id) {
        if (id <= 0) {
            throw new ApplicationException(HttpStatus.NOT_FOUND, "User id must be specified.");
        }
        return ResponseEntity.ok(userService.getChatRoomsByUserId(id));
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public ResponseEntity<User> createUser(
            @RequestParam(value = "sort") String sort,
            @RequestBody UserRequest userRequest) {
        if (userRequest.username() == null ||
                userRequest.password() == null ||
                userRequest.email() == null) {
            throw new ApplicationException(HttpStatus.BAD_REQUEST, "Username, password and email must be specified.");
        }
        if (sort.equals("new-user")) {
            LOG.debug("User {} has been successfully created.", userRequest.username());
            return ResponseEntity.ok(
                    userService.createUser(userRequest.username(), userRequest.password(), userRequest.email())
            );
        } else if (sort.equals("new-admin")) {
            LOG.debug("Admin {} has been successfully created.", userRequest.username());
            return ResponseEntity.ok(
                    userService.createAdmin(userRequest.username(), userRequest.password(), userRequest.email())
            );
        } else {
            throw new ApplicationException(HttpStatus.BAD_REQUEST,
                    "Set the sort type to user or admin to create a user or an admin.");
        }
    }

    @ResponseStatus(HttpStatus.ACCEPTED)
    @PostMapping(path = "/{id}/avatar")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    public void uploadAvatar(@PathVariable Long id, @RequestBody UserRequest userRequest) {
        if (id <= 0) {
            throw new ApplicationException(HttpStatus.NOT_FOUND, "User id must be specified.");
        }
        if (userRequest.avatar() == null) {
            throw new ApplicationException(HttpStatus.BAD_REQUEST, "Avatar must be specified.");
        }
        userService.uploadAvatar(id, userRequest.avatar());
        LOG.debug("Avatar has been successfully uploaded.");
    }

    // ------------------------------------------------------
    // TODO: Should be used in Spring security (authorization) for user visibility (online/offline)
    // TODO: After should be used in React.js for demonstrating user status (online/offline)
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/login")
    public void login(@RequestBody UserRequest userRequest) {
        userService.loginUser(userRequest.username(), userRequest.password());
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/logout")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    public ResponseEntity<Void> logout(@RequestParam String username) {
        userService.logoutUser(username);
        return ResponseEntity.ok().build();
    }
    // ------------------------------------------------------

    @ResponseStatus(HttpStatus.OK)
    @PutMapping(path = "/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    public void updateUserById(@PathVariable Long id, UserRequest userRequest) {
        if (id <= 0) {
            throw new ApplicationException(HttpStatus.NOT_FOUND, "User id must be specified.");
        }
        if (userRequest.username() == null ||
                userRequest.password() == null ||
                userRequest.email() == null) {
            throw new ApplicationException(HttpStatus.BAD_REQUEST, "Username, password and email must be specified.");
        }
        userService.updateUserById((id), userRequest.username(), userRequest.password(), userRequest.email());
        LOG.debug("User {} has been successfully updated.", userRequest.username());
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping(path = "/{id}/status")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    public void updateUserStatusById(@PathVariable Long id, @RequestParam UserStatus status) {
        if (id <= 0) {
            throw new ApplicationException(HttpStatus.NOT_FOUND, "User id must be specified.");
        }
        userService.updateUserStatusById(id, status);
        LOG.debug("User status has been successfully updated.");
    }

    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping(path = "/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public void deleteUserById(@PathVariable Long id) {
        if (id <= 0) {
            throw new ApplicationException(HttpStatus.NOT_FOUND, "User id must be specified.");
        }
        userService.deleteUserById(id);
        LOG.debug("User has been successfully deleted.");
    }
}