package com.example.webchat.controller;

import com.example.webchat.dto.request.NotificationRequest;
import com.example.webchat.entity.Notification;
import com.example.webchat.exception.ApplicationException;
import com.example.webchat.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/notifications")
@PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
public class NotificationController {

    private static final Logger LOG = LoggerFactory.getLogger(NotificationController.class);

    private final NotificationService notificationService;

    @Autowired
    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(path = "/")
    public ResponseEntity<Iterable<Notification>> getAllNotifications() {
        return ResponseEntity.ok(notificationService.getAllNotifications());
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(path = "/{id}")
    public ResponseEntity<Notification> getNotificationById(@PathVariable Long id) {
        if (id <= 0) {
            throw new ApplicationException(HttpStatus.NOT_FOUND, "Notification id must be specified.");
        }
        return ResponseEntity.ok(notificationService.getNotificationById(id));
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public ResponseEntity<Iterable<Notification>> getUnreadNotifications(
            @RequestAttribute(value = "sort") String sort) {
        if (sort.equals("unread")) {
            return ResponseEntity.ok(notificationService.getUnreadNotifications());
        } else if (sort.equals("read")) {
            return ResponseEntity.ok(notificationService.getReadNotifications());
        } else {
            throw new ApplicationException(HttpStatus.NOT_FOUND, "Sort must be specified (read/unread).");
        }
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(path = "/pagination")
    public ResponseEntity<Iterable<Notification>> getPaginationNotifications(
            @RequestParam(value = "limit") int limit,
            @RequestParam(value = "offset") int offset) {
        if (limit <= 0) {
            throw new ApplicationException(HttpStatus.NOT_FOUND, "Limit must be specified.");
        }
        if (offset <= 0) {
            throw new ApplicationException(HttpStatus.NOT_FOUND, "Offset must be specified.");
        }
        return ResponseEntity.ok(notificationService.getPaginationNotifications(limit, offset));
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public ResponseEntity<Notification> createNotification(@RequestBody NotificationRequest notificationRequest) {
        if (notificationRequest.content().isEmpty()) {
            throw new ApplicationException(HttpStatus.NOT_FOUND, "Notification content must be specified.");
        }
        LOG.debug("Notification has been successfully created.");
        return ResponseEntity.ok(notificationService.createNotification(notificationRequest.content()));
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping(path = "/{id}")
    public void updateNotification(@PathVariable Long id, @RequestBody NotificationRequest notificationRequest) {
        if (id <= 0) {
            throw new ApplicationException(HttpStatus.NOT_FOUND, "Notification id must be specified.");
        }
        if (notificationRequest.content().isEmpty()) {
            throw new ApplicationException(HttpStatus.NOT_FOUND, "Notification content must be specified.");
        }
        LOG.debug("Notification has been successfully updated.");
        notificationService.updateNotification(id, notificationRequest.content());
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping(path = "/mark/read")
    public void markAllAsRead() {
        LOG.debug("All notifications have been successfully marked as read.");
        notificationService.markAllAsRead();
    }

    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping(path = "/{id}")
    public void deleteNotification(@PathVariable Long id) {
        if (id <= 0) {
            throw new ApplicationException(HttpStatus.NOT_FOUND, "Notification id must be specified.");
        }
        LOG.debug("Notification has been successfully deleted.");
        notificationService.deleteNotification(id);
    }

    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping
    public void deleteAllNotifications() {
        LOG.debug("All notifications have been successfully deleted.");
        notificationService.deleteAllNotifications();
    }
}
