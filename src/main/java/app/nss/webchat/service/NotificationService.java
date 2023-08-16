package app.nss.webchat.service;

import app.nss.webchat.entity.Notification;
import app.nss.webchat.entity.NotificationStatus;
import app.nss.webchat.entity.User;
import app.nss.webchat.repository.NotificationRepository;
import app.nss.webchat.repository.UserRepository;
import app.nss.webchat.exception.ApplicationException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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

    @Cacheable(value = "notificationsCache", key = "{#root.methodName, #root.args}")
    public List<Notification> getAllNotifications() {
        return notificationRepository.findAll();
    }

    @Cacheable(value = "notificationsCache", key = "{#root.methodName, #root.args}")
    public Notification getNotificationById(Long id) {
        return notificationRepository.findById(id).orElseThrow(
                () -> new ApplicationException(HttpStatus.NOT_FOUND, "Notification with id " + id + " not found.")
        );
    }

    @Cacheable(value = "notificationsCache", key = "'unreadNotifications'")
    public List<Notification> getUnreadNotifications(Long recipientId) {
        userRepository.findById(recipientId).orElseThrow(
                () -> new ApplicationException(
                        HttpStatus.NOT_FOUND, "Recipient with id " + recipientId + " not found.")
        );
        return notificationRepository.findByRecipientIdAndStatus(recipientId, NotificationStatus.UNREAD);
    }

    @Cacheable(value = "notificationsCache", key = "'readNotifications'")
    public List<Notification> getReadNotifications(Long recipientId) {
        userRepository.findById(recipientId).orElseThrow(
                () -> new ApplicationException(
                        HttpStatus.NOT_FOUND, "Recipient with id " + recipientId + " not found.")
        );
        return notificationRepository.findByRecipientIdAndStatus(recipientId, NotificationStatus.READ);
    }


    @Cacheable(value = "notificationsCache", key = "'paginationNotifications' + #limit + #offset")
    public List<Notification> getPaginationNotifications(int limit, int offset) {
        List<Notification> allNotifications = notificationRepository.findAll();
        return paginationService.getPaginatedList(allNotifications, limit, offset);
    }

    @CachePut(value = "notificationsCache", key = "#result.id")
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
        notification.setStatus(NotificationStatus.UNREAD);
        notification.setTimeStamp(LocalDateTime.now());
        return notificationRepository.save(notification);
    }

    @CacheEvict(value = "notificationsCache", key = "#id")
    @CachePut(value = "notificationsCache", key = "#id")
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

    @CacheEvict(value = "notificationsCache", allEntries = true)
    @Transactional
    public void markAllAsRead(Long recipientId) {
        userRepository.findById(recipientId).orElseThrow(
                () -> new ApplicationException(
                        HttpStatus.NOT_FOUND, "Recipient with id " + recipientId + " not found.")
        );
        notificationRepository.updateStatus(NotificationStatus.READ, NotificationStatus.UNREAD, recipientId);
    }

    @CacheEvict(value = "notificationsCache", allEntries = true)
    @Transactional
    public void markAllAsUnread(Long recipientId) {
        userRepository.findById(recipientId).orElseThrow(
                () -> new ApplicationException(
                        HttpStatus.NOT_FOUND, "Recipient with id " + recipientId + " not found.")
        );
        notificationRepository.updateStatus(NotificationStatus.UNREAD, NotificationStatus.READ, recipientId);
    }

    @CacheEvict(value = "notificationsCache", key = "#id")
    public void deleteNotification(Long id) {
        Notification notification = notificationRepository.findById(id).orElseThrow(
                () -> new ApplicationException(HttpStatus.NOT_FOUND, "Notification with id " + id + " not found.")
        );
        notificationRepository.delete(notification);
    }

    @CacheEvict(value = "notificationsCache", key = "#userId")
    public void deleteAllNotificationsFromUser(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new ApplicationException(HttpStatus.NOT_FOUND, "User with id " + userId + " not found.")
        );
        List<Notification> userNotifications = user.getNotifications();
        notificationRepository.deleteAll(userNotifications);
    }
}
