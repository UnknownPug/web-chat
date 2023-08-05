package app.nss.webchat.controller;

import app.nss.webchat.entity.Message;
import app.nss.webchat.exception.ApplicationException;
import app.nss.webchat.service.MessageService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(MessageController.class)
public class MessageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MessageService messageService;

    @Test
    @WithMockUser(roles = "USER")
    public void testGetMessageById_ValidRequest() throws Exception {
        // Arrange
        long messageId = 1L;
        Message message = new Message();

        message.setId(messageId);
        message.setContent("Hello, world!");

        when(messageService.getMessageById(messageId)).thenReturn(message);

        // Act
        ResultActions resultActions = mockMvc.perform(get("/message/{id}", messageId)
                .accept(MediaType.APPLICATION_JSON)); // Use accept() for response format

        // Assert
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(messageId))
                .andExpect(jsonPath("$.content").value(message.getContent()));
    }

    @Test
    @WithMockUser(roles = "USER")
    public void testGetMessageById_InvalidRequest() throws Exception {
        // Arrange
        long messageId = 0L;

        when(messageService.getMessageById(messageId)).thenThrow(new ApplicationException(
                HttpStatus.NOT_FOUND, "Message id must be specified."));

        // Act
        ResultActions resultActions = mockMvc.perform(get("/messages/{id}", messageId)
                .contentType(MediaType.APPLICATION_JSON));

        // Assert
        resultActions.andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "USER")
    public void testGetFilteredMessages_FilterUserSortDesc_ValidRequest() throws Exception {
        // Arrange
        long userId = 1L;
        Message message1 = new Message();
        Message message2 = new Message();

        message1.setId(userId);
        message1.setContent("Hello, world!");

        message2.setId(2L);
        message2.setContent("How are you?");

        List<Message> messages = new ArrayList<>();
        messages.add(message1);
        messages.add(message2);

        when(messageService.getSortedMessagesForUserDesc(userId)).thenReturn(messages);

        // Act and Assert
        mockMvc.perform(get("/message/filter/{id}", userId)
                        .param("type", "user")
                        .param("sort", "desc")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(messages.size()));
    }

    @Test
    @WithMockUser(roles = "USER")
    public void testDeleteMessage_ForbiddenRequest() throws Exception {
        // Arrange
        Long messageId = 1L;
        doNothing().when(messageService).deleteMessage(messageId);

        // Act and Assert
        mockMvc.perform(delete("/message/{id}", messageId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }
}

