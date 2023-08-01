package com.example.webchat.service;

import com.example.webchat.entity.Notification;
import com.example.webchat.entity.User;
import com.example.webchat.exception.ApplicationException;
import com.example.webchat.repository.NotificationRepository;
import com.example.webchat.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final PaginationService paginationService;
    private final UserRepository userRepository;

    @Autowired
    public NotificationService(NotificationRepository notificationRepository, PaginationService paginationService,
                               UserRepository userRepository) {
        this.notificationRepository = notificationRepository;
        this.paginationService = paginationService;
        this.userRepository = userRepository;
    }

    public List<Notification> getAllNotifications() {
        return notificationRepository.findAll();
    }

    public Notification getNotificationById(Long id) {
        return notificationRepository.findById(id).orElseThrow(
                () -> new ApplicationException(HttpStatus.NOT_FOUND, "Notification with id " + id + " not found.")
        );
    }

    public List<Notification> getUnreadNotifications() {
        return notificationRepository.findAllByStatusIsFalse();
    }

    public List<Notification> getReadNotifications() {
        return notificationRepository.findAllByStatusIsTrue();
    }

    public List<Notification> getPaginationNotifications(int limit, int offset) {
        List<Notification> allNotifications = notificationRepository.findAll();
        return paginationService.getPaginatedList(allNotifications, limit, offset);
    }

    public Notification createNotification(String content, Long recipientId) {
        Notification notification = new Notification();
        User recipient = userRepository.findById(recipientId).orElseThrow(
                () -> new ApplicationException(HttpStatus.NOT_FOUND, "User with id " + recipientId + " not found.")
        );
        if (content.isEmpty()) {
            throw new ApplicationException(HttpStatus.BAD_REQUEST, "Content must be defined.");
        }
        notification.setContent(content);
        notification.setRecipient(recipient);
        return notificationRepository.save(notification);
    }

    public void updateNotification(Long id, String content) {
        Notification notification = notificationRepository.findById(id).orElseThrow(
                () -> new ApplicationException(HttpStatus.NOT_FOUND, "Notification with id " + id + " not found.")
        );
        if (content != null && !content.isEmpty() && !Objects.equals(notification.getContent(), content)) {
            notification.setContent(content);
        } else {
            throw new ApplicationException(HttpStatus.BAD_REQUEST,
                    "Notification must be defined or should not be the same as the existing notification.");
        }
        notificationRepository.save(notification);
    }

    @Transactional
    public void markAllAsRead() {
        notificationRepository.updateStatusFromFalseToTrue();
    }

    @Transactional
    public void markAllAsUnread() {
        notificationRepository.updateStatusFromTrueToFalse();
    }

    public void deleteNotification(Long id) {
        Notification notification = notificationRepository.findById(id).orElseThrow(
                () -> new ApplicationException(HttpStatus.NOT_FOUND, "Notification with id " + id + " not found.")
        );
        notificationRepository.delete(notification);
    }

    public void deleteAllNotificationsFromUser(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new ApplicationException(HttpStatus.NOT_FOUND, "User with id " + userId + " not found.")
        );
        List<Notification> userNotifications = user.getNotification();
        notificationRepository.deleteAll(userNotifications);
    }
}
