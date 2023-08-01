package com.example.webchat.service;

import com.example.webchat.entity.User;
import com.example.webchat.exception.ApplicationException;
import com.example.webchat.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    // Test getUsers method
    @Test
    public void testGetUsers() {
        List<User> mockUserList = new ArrayList<>();

        // Mock the behavior of userRepository.findAll()
        when(userRepository.findAll()).thenReturn(mockUserList);

        List<User> users = userService.getUsers();

        assertEquals(mockUserList.size(), users.size());
    }

    // Test getUserById method - user not found
    @Test
    public void testGetUserById_UserNotFound() {
        Long userId = 99L;
        // Mock the behavior of userRepository.findById()
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(ApplicationException.class, () -> userService.getUserById(userId));
    }

    // Test getChatRoomsByUserId method - user not found
    @Test
    public void testGetChatRoomsByUserId_UserNotFound() {
        Long userId = 99L;
        // Mock the behavior of userRepository.findById()
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(ApplicationException.class, () -> userService.getChatRoomsByUserId(userId));
    }

    // Test updateUserById method - empty username
    @Test
    public void testUpdateUserById_EmptyUsername() {
        Long userId = 1L;
        String password = "newPassword";
        String email = "newemail@example.com";

        assertThrows(ApplicationException.class, () -> userService.updateUserById(userId, "", password, email));
    }

    // Test updateUserById method - password not changed
    @Test
    public void testUpdateUserById_PasswordNotChanged() {
        Long userId = 1L;
        String username = "newUsername";
        String email = "newemail@example.com";

        User mockUser = new User();
        mockUser.setId(userId);
        mockUser.setUsername(username);
        mockUser.setEmail(email);
        mockUser.setPassword("existingPassword");
        // Mock the behavior of userRepository.findById()
        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));

        assertThrows(ApplicationException.class, () -> userService.updateUserById(userId, username, "existingPassword", email));
    }

    // Test updateUserStatusById method - null status
    @Test
    public void testUpdateUserStatusById_NullStatus() {
        Long userId = 1L;

        assertThrows(ApplicationException.class, () -> userService.updateUserStatusById(userId, null));
    }

    // Test deleteUserById method - user not found
    @Test
    public void testDeleteUserById_UserNotFound() {
        Long userId = 99L;

        // Mock the behavior of userRepository.findById()
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(ApplicationException.class, () -> userService.deleteUserById(userId));
    }
}

