package app.nss.webchat.controller;

import app.nss.webchat.entity.Notification;
import app.nss.webchat.entity.User;
import app.nss.webchat.service.NotificationService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(NotificationController.class)
@AutoConfigureMockMvc
@WithMockUser(roles = {"USER", "ADMIN"})
public class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NotificationService notificationService;

    @Test
    public void testGetAllNotifications_ValidRequest() throws Exception {
        // Arrange
        List<Notification> notifications = new ArrayList<>();

        Notification notification1 = new Notification();
        notification1.setId(1L);
        notification1.setContent("Notification 1");

        Notification notification2 = new Notification();
        notification2.setId(2L);
        notification2.setContent("Notification 2");

        notifications.add(notification1);
        notifications.add(notification2);

        when(notificationService.getAllNotifications()).thenReturn(notifications);

        // Act and Assert
        mockMvc.perform(get("/notifications")
                        .accept(MediaType.APPLICATION_JSON)) // Use accept() for response format
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(notifications.size()))
                .andExpect(jsonPath("$[0].id").value(notification1.getId()))
                .andExpect(jsonPath("$[0].content").value(notification1.getContent()))
                .andExpect(jsonPath("$[1].id").value(notification2.getId()))
                .andExpect(jsonPath("$[1].content").value(notification2.getContent()));
    }

    @Test
    public void testGetNotificationById_ValidRequest() throws Exception {
        // Arrange
        long notificationId = 1L;
        Notification notification = new Notification();
        notification.setId(notificationId);
        notification.setContent("Notification 1");

        when(notificationService.getNotificationById(notificationId)).thenReturn(notification);

        // Act and Assert
        mockMvc.perform(get("/notifications/{id}", notificationId)
                        .accept(MediaType.APPLICATION_JSON)) // Use accept() for response format
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(notification.getId()))
                .andExpect(jsonPath("$.content").value(notification.getContent()));
    }

    @Test
    public void testGetUnreadNotifications_InvalidSortValue() throws Exception {
        // Arrange
        String invalidSort = "invalidSortValue";

        // Act and Assert
        mockMvc.perform(get("/notifications/")
                        .param("sort", invalidSort)
                        .accept(MediaType.APPLICATION_JSON)) // Use accept() for response format
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGetPaginationNotifications_InvalidLimit() throws Exception {
        // Arrange
        int limit = 0; // Invalid limit
        int offset = 0;

        // Act and Assert
        mockMvc.perform(get("/notifications/pagination")
                        .param("limit", String.valueOf(limit))
                        .param("offset", String.valueOf(offset))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGetPaginationNotifications_InvalidOffset() throws Exception {
        // Arrange
        int limit = 10;
        int offset = -1; // Invalid offset

        // Act and Assert
        mockMvc.perform(get("/notifications/pagination")
                        .param("limit", String.valueOf(limit))
                        .param("offset", String.valueOf(offset))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testCreateNotification_EmptyContent() throws Exception {
        // Arrange
        Notification notification = new Notification();
        notification.setContent("");

        User recipient = new User();
        recipient.setId(1L);

        notification.setRecipient(recipient);

        // Act and Assert
        mockMvc.perform(post("/notifications")
                        .content(asJsonString(notification))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    public void testCreateNotification_InvalidRecipientId() throws Exception {
        // Arrange
        Notification notification = new Notification();
        notification.setContent("New notification");

        User recipient = new User();
        recipient.setId(null);

        notification.setRecipient(recipient);


        // Act and Assert
        mockMvc.perform(post("/notifications")
                        .content(asJsonString(notification))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    public void testUpdateNotification_Forbidden() throws Exception {
        // Arrange
        long notificationId = 1L;
        Notification notification = new Notification();
        notification.setContent(""); // Empty content

        // Act and Assert
        mockMvc.perform(put("/notifications/{id}", notificationId)
                        .content(asJsonString(notification))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    // Helper method to convert object to JSON string
    private String asJsonString(Object object) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(object);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testMarkAllAsRead_isForbidden() throws Exception {
        // Act and Assert
        mockMvc.perform(put("/notifications/")
                        .param("mark", "invalid"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testDeleteNotification_InvalidId_isForbidden() throws Exception {
        // Arrange
        Long notificationId = -1L;

        // Act and Assert
        mockMvc.perform(delete("/notifications/{id}", notificationId))
                .andExpect(status().isForbidden());
        verify(notificationService, never()).deleteNotification(notificationId);
    }

    @Test
    public void testDeleteAllNotificationsFromUser_InvalidUserId_Forbidden() throws Exception {
        // Arrange
        Long userId = -1L;

        // Act and Assert
        mockMvc.perform(delete("/notifications/user/{id}", userId))
                .andExpect(status().isForbidden());
        verify(notificationService, never()).deleteAllNotificationsFromUser(userId);
    }
}
