package app.nss.webchat.service;

import app.nss.webchat.entity.Notification;
import app.nss.webchat.entity.User;
import app.nss.webchat.exception.ApplicationException;
import app.nss.webchat.repository.NotificationRepository;
import app.nss.webchat.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@SpringBootTest
public class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private NotificationService notificationService;

    @Test
    public void testGetAllNotifications() {
        // Mock the repository response
        Notification notification1 = new Notification();
        notification1.setId(1L);
        notification1.setContent("Notification 1");

        Notification notification2 = new Notification();
        notification2.setId(2L);
        notification2.setContent("Notification 2");

        List<Notification> expectedNotifications = List.of(
                notification1,
                notification2
        );
        when(notificationRepository.findAll()).thenReturn(expectedNotifications);

        // Call the service method
        List<Notification> actualNotifications = notificationService.getAllNotifications();

        // Assert the result
        assertEquals(expectedNotifications, actualNotifications);
    }

    @Test
    public void testGetNotificationById() {
        // Mock the repository response
        Long notificationId = 1L;
        Notification expectedNotification = new Notification();
        expectedNotification.setId(notificationId);
        expectedNotification.setContent("Sample notification");
        when(notificationRepository.findById(notificationId)).thenReturn(Optional.of(expectedNotification));

        // Call the service method
        Notification actualNotification = notificationService.getNotificationById(notificationId);

        // Assert the result
        assertEquals(expectedNotification, actualNotification);
    }

    @Test
    public void testGetNotificationById_NotFound() {
        // Mock the repository response for a non-existing ID
        Long nonExistingId = 100L;
        when(notificationRepository.findById(nonExistingId)).thenReturn(Optional.empty());

        // Call the service method and assert it throws the expected ApplicationException
        assertThrows(ApplicationException.class, () -> notificationService.getNotificationById(nonExistingId));
    }

    @Test
    public void testCreateNotification_Success() {
        // Mock the repository response for the recipient
        Long recipientId = 1L;
        User recipient = new User();
        recipient.setId(recipientId);
        recipient.setUsername("Recipient");
        when(userRepository.findById(recipientId)).thenReturn(Optional.of(recipient));

        // Mock the repository save response
        Notification savedNotification = new Notification();
        savedNotification.setId(1L);
        savedNotification.setContent("Notification Content");
        savedNotification.setRecipient(recipient);
        when(notificationRepository.save(any(Notification.class))).thenReturn(savedNotification);

        // Call the service method
        String content = "Notification Content";
        Notification createdNotification = notificationService.createNotification(content, recipientId);

        // Assert the result
        assertEquals(savedNotification, createdNotification);
        assertEquals(content, createdNotification.getContent());
        assertEquals(recipient, createdNotification.getRecipient());
    }

    @Test
    public void testCreateNotification_RecipientNotFound() {
        // Mock the repository response for a non-existing recipient ID
        Long nonExistingRecipientId = 100L;
        when(userRepository.findById(nonExistingRecipientId)).thenReturn(Optional.empty());

        // Call the service method and assert it throws the expected ApplicationException
        assertThrows(ApplicationException.class, () -> notificationService.createNotification("Notification Content", nonExistingRecipientId));
    }

    @Test
    public void testCreateNotification_EmptyContent() {
        // Call the service method with an empty content and assert it throws the expected ApplicationException
        assertThrows(ApplicationException.class, () -> notificationService.createNotification("", 1L));
    }

    @Test
    public void testUpdateNotification_NotFound() {
        // Mock the repository response for a non-existing notification ID
        Long nonExistingId = 100L;
        when(notificationRepository.findById(nonExistingId)).thenReturn(Optional.empty());

        // Call the service method and assert it throws the expected ApplicationException
        assertThrows(ApplicationException.class, () -> notificationService.updateNotification(nonExistingId, "New content"));
    }

    @Test
    public void testUpdateNotification_EmptyContent() {
        // Call the service method with an empty content and assert it throws the expected ApplicationException
        assertThrows(ApplicationException.class, () -> notificationService.updateNotification(1L, ""));
    }

    @Test
    public void testUpdateNotification_SameContent() {
        // Mock the repository response for the existing notification with the same content
        Long notificationId = 1L;
        String content = "Existing content";
        Notification existingNotification = new Notification();
        existingNotification.setId(notificationId);
        existingNotification.setContent(content);
        when(notificationRepository.findById(notificationId)).thenReturn(Optional.of(existingNotification));

        // Call the service method and assert it throws the expected ApplicationException
        assertThrows(ApplicationException.class, () -> notificationService.updateNotification(notificationId, content));
    }

    @Test
    public void testMarkAllAsRead_RecipientNotFound() {
        Long recipientId = 1L;

        // Mock the findById method to return an empty Optional
        when(notificationRepository.findById(recipientId))
                .thenReturn(Optional.empty());

        // Call the method and expect an ApplicationException to be thrown
        assertThrows(ApplicationException.class, () -> notificationService.markAllAsRead(recipientId));

        // Verify that updateStatus is not called
        verify(notificationRepository, never())
                .updateStatus(any(), any(), any());
    }

    @Test
    public void testMarkAllAsUnread_RecipientNotFound() {
        Long recipientId = 1L;

        // Mock the findById method to return an empty Optional
        when(notificationRepository.findById(recipientId))
                .thenReturn(Optional.empty());

        // Call the method and expect an ApplicationException to be thrown
        assertThrows(ApplicationException.class, () -> notificationService.markAllAsUnread(recipientId));

        // Verify that updateStatus is not called
        verify(notificationRepository, never())
                .updateStatus(any(), any(), any());
    }

    @Test
    public void testDeleteAllNotificationsFromUser_UserNotFound() {
        // Mock the repository response for a non-existing user ID
        Long nonExistingUserId = 100L;
        when(userRepository.findById(nonExistingUserId)).thenReturn(Optional.empty());

        // Call the service method and assert it throws the expected ApplicationException
        assertThrows(ApplicationException.class, () -> notificationService.deleteAllNotificationsFromUser(nonExistingUserId));
    }
}

