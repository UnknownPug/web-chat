package com.example.webchat.controller;

import com.example.webchat.entity.ChatRoom;
import com.example.webchat.exception.ApplicationException;
import com.example.webchat.service.ChatRoomService;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(SpringExtension.class)
@WebMvcTest(ChatRoomController.class)
public class ChatRoomControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ChatRoomService chatRoomService;

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testGetChatRooms() throws Exception {
        // Arrange
        List<ChatRoom> mockChatRooms = new ArrayList<>();

        ChatRoom room1 = new ChatRoom();
        room1.setName("Room1");

        ChatRoom room2 = new ChatRoom();
        room2.setName("Room2");

        mockChatRooms.add(room1);
        mockChatRooms.add(room2);

        when(chatRoomService.getChatRooms()).thenReturn(mockChatRooms);

        // Act
        ResultActions resultActions = mockMvc.perform(get("/chat-rooms")
                .contentType(MediaType.APPLICATION_JSON));

        // Assert
        resultActions.andExpect(status().isOk())
                // Assuming there are 2 rooms in the response
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value("Room1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].name").value("Room2"));
    }

    @Test
    @WithMockUser(roles = "USER")
    public void testGetChatRoomById_ValidId() throws Exception {
        // Arrange
        Long roomId = 1L;

        ChatRoom mockChatRoom = new ChatRoom();
        mockChatRoom.setName("Room 1");

        when(chatRoomService.getChatRoomById(roomId)).thenReturn(mockChatRoom);

        // Act
        ResultActions resultActions = mockMvc.perform(get("/chat-rooms/{id}", roomId)
                .contentType(MediaType.APPLICATION_JSON));

        // Assert
        resultActions.andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Room 1"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testGetChatRoomById_InvalidId() throws Exception {
        // Arrange
        Long invalidRoomId = -1L;

        when(chatRoomService.getChatRoomById(invalidRoomId)).thenThrow(new ApplicationException(
                HttpStatus.NOT_FOUND, "ChatRoom id must be specified."));

        // Act
        ResultActions resultActions = mockMvc.perform(get("/chat-rooms/{id}", invalidRoomId)
                .contentType(MediaType.APPLICATION_JSON));

        // Assert
        resultActions.andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "USER")
    public void testGetChatRoomByName_ValidName() throws Exception {
        // Arrange
        String roomName = "Room 1";
        ChatRoom mockChatRoom = new ChatRoom();
        mockChatRoom.setName(roomName);

        when(chatRoomService.getChatRoomByName(roomName)).thenReturn(mockChatRoom);

        // Act
        ResultActions resultActions = mockMvc.perform(get("/chat-rooms/name/{name}", roomName)
                .contentType(MediaType.APPLICATION_JSON));

        // Assert
        resultActions.andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(roomName));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testGetChatRoomByName_NullName() throws Exception {

        when(chatRoomService.getChatRoomByName(null)).thenThrow(
                new ApplicationException(HttpStatus.NOT_FOUND, "ChatRoom name must be specified.")
        );

        // Act
        ResultActions resultActions = mockMvc.perform(
                get("/chat-rooms/name/{name}", (Object) null)
                .contentType(MediaType.APPLICATION_JSON));

        // Assert
        resultActions.andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testGetChatRoomByUser_EmptyUsername() throws Exception {
        // Arrange
        String emptyUsername = "";

        when(chatRoomService.getChatRoomByUser(emptyUsername)).thenThrow(new ApplicationException(HttpStatus.NOT_FOUND, "User name must be specified."));

        // Act
        ResultActions resultActions = mockMvc.perform(get("/chat-rooms/user/{username}", emptyUsername)
                .contentType(MediaType.APPLICATION_JSON));

        // Assert
        resultActions.andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "USER")
    public void testUpdateChatRoom_ForbiddenRequest() throws Exception {
        // Arrange
        long roomId = 1L;

        ChatRoom chatRoom = new ChatRoom();
        chatRoom.setName("Updated Room");
        chatRoom.setDescription("This is an updated chat room description.");

        doNothing().when(chatRoomService).updateChatRoom(anyLong(), anyString(), anyString());

        // Convert ChatRoomRequest to JSON
        ObjectMapper objectMapper = new ObjectMapper();
        String requestBody = objectMapper.writeValueAsString(chatRoom);

        // Act
        ResultActions resultActions = mockMvc.perform(put("/chat-rooms/{id}", roomId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody));

        // Assert
        resultActions.andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "USER")
    public void testDeleteChatRoom_ForbiddenRequest() throws Exception {
        // Arrange
        long roomId = 1L;

        doNothing().when(chatRoomService).deleteChatRoom(anyLong());

        // Act
        ResultActions resultActions = mockMvc.perform(delete("/chat-rooms/{id}", roomId));

        // Assert
        resultActions.andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "USER")
    public void testDeleteParticipant_ForbiddenRequest() throws Exception {
        // Arrange
        long roomId = 1L;
        long userId = 123L;

        doNothing().when(chatRoomService).deleteParticipant(anyLong(), anyLong());

        // Act
        ResultActions resultActions =
                mockMvc.perform(delete("/chat-rooms/{id}/participants/{userId}", roomId, userId));

        // Assert
        resultActions.andExpect(status().isForbidden());
    }
}

