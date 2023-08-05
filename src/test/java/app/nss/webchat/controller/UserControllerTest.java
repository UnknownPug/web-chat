package app.nss.webchat.controller;

import app.nss.webchat.entity.User;
import app.nss.webchat.entity.UserStatus;
import app.nss.webchat.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testGetUsers_ValidRequest() throws Exception {
        // Arrange
        List<User> users = new ArrayList<>();
        User user1 = new User();
        user1.setId(1L);
        user1.setUsername("user1");
        users.add(user1);

        User user2 = new User();
        user2.setId(2L);
        user2.setUsername("user2");
        users.add(user2);

        when(userService.getUsers()).thenReturn(users);

        // Act and Assert
        mockMvc.perform(get("/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(users.size()))
                .andExpect(jsonPath("$[0].id").value(user1.getId()))
                .andExpect(jsonPath("$[0].username").value(user1.getUsername()))
                .andExpect(jsonPath("$[1].id").value(user2.getId()))
                .andExpect(jsonPath("$[1].username").value(user2.getUsername()));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testGetUserById_ValidRequest() throws Exception {
        // Arrange
        long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setUsername("user1");

        when(userService.getUserById(userId)).thenReturn(user);

        // Act and Assert
        mockMvc.perform(get("/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(user.getId()))
                .andExpect(jsonPath("$.username").value(user.getUsername()));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testGetUserById_InvalidId() throws Exception {
        // Arrange
        long userId = -1L;

        // Act and Assert
        mockMvc.perform(get("/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testGetUserByUsername_InvalidUsername() throws Exception {

        // Act and Assert
        mockMvc.perform(get("/users/name/{username}", (Object) null)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testGetUserByEmail_InvalidEmail() throws Exception {

        // Act and Assert
        mockMvc.perform(get("/users/email/{email}", (Object) null)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testGetUserByKeyword_InvalidKeyword() throws Exception {
        // Act and Assert
        mockMvc.perform(get("/users/search")
                        .param("keyword", (String) null)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testGetChatRoomsByUserId_InvalidUserId() throws Exception {
        // Arrange
        long userId = 0L;

        // Act and Assert
        mockMvc.perform(get("/users/{id}/chat-rooms", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "USER")
    public void testUploadAvatar_ValidRequest_Forbidden() throws Exception {
        // Arrange
        long userId = 1L;
        String avatarContent = "Some binary data for the avatar";
        MockMultipartFile avatarFile = new MockMultipartFile("avatar", "avatar.png", "image/png", avatarContent.getBytes());

        // Act and Assert
        mockMvc.perform(multipart("/users/{id}/avatar", userId)
                        .file(avatarFile)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isForbidden());
        // Add more assertions as needed for validating the response and behavior
    }

    @Test
    @WithMockUser(roles = "USER")
    public void testLogin_ValidRequest_isForbidden() throws Exception {
        // Arrange
        String username = "user123";
        User userRequest = new User();
        userRequest.setUsername(username);

        // Act and Assert
        mockMvc.perform(post("/login")
                        .content(asJsonString(userRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "USER")
    public void testLogout_ValidRequest_Forbidden() throws Exception {
        // Arrange
        String username = "user123";
        User userRequest = new User();
        userRequest.setUsername(username);

        // Act and Assert
        mockMvc.perform(post("/logout")
                        .content(asJsonString(userRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "USER")
    public void testUpdateUserStatusById_ValidRequest_Forbidden() throws Exception {
        // Arrange
        long userId = 1L;
        User userRequest = new User();
        userRequest.setUserStatus(UserStatus.ONLINE);

        // Act and Assert
        mockMvc.perform(put("/users/{id}/status", userId)
                        .content(asJsonString(userRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    // Helper method to convert an object to JSON string
    private String asJsonString(Object object) {
        try {
            return new ObjectMapper().writeValueAsString(object);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testDeleteUserById_ValidRequest_Forbidden() throws Exception {
        // Arrange
        long userId = 1L;

        // Act and Assert
        mockMvc.perform(delete("/users/{id}", userId))
                .andExpect(status().isForbidden());
    }
}

