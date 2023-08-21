package app.nss.webchat.controller;

import app.nss.webchat.dto.request.UserRequest;
import app.nss.webchat.entity.User;
import app.nss.webchat.exception.ApplicationException;
import app.nss.webchat.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping(path = "/users")
public class UserController {

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
    public ResponseEntity<User> getUserById(@PathVariable(value = "id") Long id) {
        if (id <= 0) {
            throw new ApplicationException(HttpStatus.NOT_FOUND, "User id must be specified.");
        }
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(path = "/search")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    public ResponseEntity<User> getUserByUserIdentifier(@RequestParam(value = "identifier") String identifier) {
        if (identifier == null) {
            throw new ApplicationException(
                    HttpStatus.NOT_FOUND, "You must specify user identifier: email/username");
        }
        return ResponseEntity.ok(userService.getUserByUserIdentifier(identifier));
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(path = "/register")
    public ResponseEntity<User> createUser(@RequestBody UserRequest request) {
        return createUserInternal(request, false);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(path = "/register-admin")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<User> createAdmin(@RequestBody UserRequest request) {
        return createUserInternal(request, true);
    }

    private ResponseEntity<User> createUserInternal(UserRequest request, boolean isAdmin) {
        if (request.username() == null || request.password() == null || request.email() == null) {
            throw new ApplicationException(HttpStatus.BAD_REQUEST, "Username, password and email must be specified.");
        }
        String userRole = isAdmin ? "Admin" : "User";
        log.info("{} {} has been successfully created.", userRole, request.username());
        if (isAdmin) {
            return ResponseEntity.ok(userService.createAdmin(request.username(), request.password(), request.email()));
        } else {
            return ResponseEntity.ok(userService.createUser(request.username(), request.password(), request.email()));
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
        log.info("Avatar has been successfully uploaded.");
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping(path = "/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    public void updateUserById(@PathVariable Long id, @RequestBody UserRequest userRequest) {
        if (id <= 0) {
            throw new ApplicationException(HttpStatus.NOT_FOUND, "User id must be specified.");
        }
        if (userRequest.username() == null ||
                userRequest.password() == null ||
                userRequest.email() == null) {
            throw new ApplicationException(HttpStatus.BAD_REQUEST, "Username, password and email must be specified.");
        }
        userService.updateUserById(id, userRequest.username(), userRequest.password(), userRequest.email());
        log.info("User {} has been successfully updated.", userRequest.username());
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping(path = "/{id}/status")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    public void updateUserStatusById(@PathVariable Long id, @RequestBody UserRequest userRequest) {
        if (id <= 0) {
            throw new ApplicationException(HttpStatus.NOT_FOUND, "User id must be specified.");
        }
        userService.updateUserStatusById(id, userRequest.status());
        log.info("User status has been successfully updated.");
    }

    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping(path = "/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public void deleteUserById(@PathVariable Long id) {
        if (id <= 0) {
            throw new ApplicationException(HttpStatus.NOT_FOUND, "User id must be specified.");
        }
        userService.deleteUserById(id);
        log.info("User has been successfully deleted.");
    }
}