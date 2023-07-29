package com.example.webchat.service;

import com.example.webchat.entity.Notification;
import com.example.webchat.exception.ApplicationException;
import com.example.webchat.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final PaginationService paginationService;

    @Autowired
    public NotificationService(NotificationRepository notificationRepository, PaginationService paginationService) {
        this.notificationRepository = notificationRepository;
        this.paginationService = paginationService;
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

    public Notification createNotification(String content) {
        Notification notification = new Notification();
        notification.setContent(content);
        return notificationRepository.save(notification);
    }

    public void updateNotification(Long id, String content) {
        Notification notification = notificationRepository.findById(id).orElseThrow(
                () -> new ApplicationException(HttpStatus.NOT_FOUND, "Notification with id " + id + " not found.")
        );
        if (content.isEmpty()) {
            throw new ApplicationException(HttpStatus.BAD_REQUEST, "Notification content must be specified.");
        }
        notification.setContent(content);
        notificationRepository.save(notification);
    }

    public void markAllAsRead() {
        notificationRepository.updateStatusFromFalseToTrue();
    }

    public void markAllAsUnread() {
        notificationRepository.updateStatusFromTrueToFalse();
    }

    public void deleteNotification(Long id) {
        Notification notification = notificationRepository.findById(id).orElseThrow(
                () -> new ApplicationException(HttpStatus.NOT_FOUND, "Notification with id " + id + " not found.")
        );
        notificationRepository.delete(notification);
    }

    public void deleteAllNotifications() {
        List<Notification> notifications = notificationRepository.findAll();
        notificationRepository.deleteAll(notifications);
    }
}
