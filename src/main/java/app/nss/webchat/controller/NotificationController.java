package app.nss.webchat.controller;

import app.nss.webchat.dto.request.NotificationRequest;
import app.nss.webchat.entity.Notification;
import app.nss.webchat.exception.ApplicationException;
import app.nss.webchat.service.NotificationService;
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
    @GetMapping
    public ResponseEntity<Iterable<Notification>> getAllNotifications() {
        return ResponseEntity.ok(notificationService.getAllNotifications());
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(path = "/{id}")
    public ResponseEntity<Notification> getNotificationById(@PathVariable(value = "id") Long id) {
        if (id <= 0) {
            throw new ApplicationException(HttpStatus.NOT_FOUND, "Notification id must be specified.");
        }
        return ResponseEntity.ok(notificationService.getNotificationById(id));
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(path = "/")
    public ResponseEntity<Iterable<Notification>> getUnreadNotifications(
            @RequestParam(value = "sort") String sort) {
        if (sort.equals("unread")) {
            return ResponseEntity.ok(notificationService.getUnreadNotifications());
        } else if (sort.equals("read")) {
            return ResponseEntity.ok(notificationService.getReadNotifications());
        } else {
            throw new ApplicationException(HttpStatus.NOT_FOUND, "Sort must be specified: read/unread.");
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

        if (notificationRequest.recipientId() == null) {
            throw new ApplicationException(HttpStatus.NOT_FOUND,
                    "Recipient not found. Please provide a valid recipient for the notification.");
        }
        LOG.debug("Notification has been successfully created.");
        return ResponseEntity.ok(notificationService.createNotification(
                notificationRequest.content(),
                notificationRequest.recipientId()));
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping(path = "/{id}")
    public void updateNotification(@PathVariable(value = "id") Long id,
                                   @RequestBody NotificationRequest notificationRequest) {
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
    @PutMapping(path = "/")
    public void markAllAsRead(@RequestParam(value = "mark") String mark) {
        if (mark.equals("read")) {
            notificationService.markAllAsRead();
            LOG.debug("All notifications have been successfully marked as read.");
        } else if (mark.equals("unread")) {
            notificationService.markAllAsUnread();
            LOG.debug("All notifications have been successfully marked as read.");
        } else {
            throw new ApplicationException(HttpStatus.NOT_FOUND, "Mark type must be specified: read/unread.");
        }
    }

    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping(path = "/{id}")
    public void deleteNotification(@PathVariable(value = "id") Long id) {
        if (id <= 0) {
            throw new ApplicationException(HttpStatus.NOT_FOUND, "Notification id must be specified.");
        }
        LOG.debug("Notification has been successfully deleted.");
        notificationService.deleteNotification(id);
    }

    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping(path = "/user/{id}")
    public void deleteAllNotificationsFromUser(@PathVariable(value = "id") Long userId) {
        if (userId <= 0) {
            throw new ApplicationException(HttpStatus.NOT_FOUND, "User id must be specified.");
        }
        LOG.debug("All notifications have been successfully deleted.");
        notificationService.deleteAllNotificationsFromUser(userId);
    }
}
