package app.nss.webchat.service;

import app.nss.webchat.entity.ChatRoom;
import app.nss.webchat.exception.ApplicationException;
import app.nss.webchat.repository.ChatRoomRepository;
import app.nss.webchat.repository.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@SpringBootTest
public class ChatRoomServiceTest {

    private ChatRoomService chatRoomService;
    private ChatRoomRepository chatRoomRepository;
    private UserRepository userRepository;

    @Before
    public void setUp() {
        chatRoomRepository = mock(ChatRoomRepository.class);
        userRepository = mock(UserRepository.class);
        chatRoomService = new ChatRoomService(chatRoomRepository, userRepository);
    }

    @Test
    public void testGetChatRooms() {
        // Arrange

        ChatRoom room1 = new ChatRoom();
        ChatRoom room2 = new ChatRoom();
        List<ChatRoom> expectedChatRooms = new ArrayList<>();

        room1.setId(1L);
        room1.setName("Room 1");
        room2.setId(2L);
        room2.setName("Room 2");
        expectedChatRooms.add(room1);
        expectedChatRooms.add(room2);
        when(chatRoomRepository.findAll()).thenReturn(expectedChatRooms);

        // Act
        List<ChatRoom> actualChatRooms = chatRoomService.getChatRooms();

        // Assert
        assertEquals(expectedChatRooms, actualChatRooms);
    }

    @Test
    public void testGetChatRoomById_ValidId() {
        // Arrange
        Long roomId = 1L;
        ChatRoom room1 = new ChatRoom();
        room1.setId(roomId);
        room1.setName("Room 1");

        when(chatRoomRepository.findById(roomId)).thenReturn(Optional.of(room1));

        // Act
        ChatRoom actualChatRoom = chatRoomService.getChatRoomById(roomId);

        // Assert
        assertEquals(room1, actualChatRoom);
    }

    @Test(expected = ApplicationException.class)
    public void testGetChatRoomById_InvalidId() {
        // Arrange
        Long roomId = 99L;
        when(chatRoomRepository.findById(roomId)).thenReturn(Optional.empty());

        // Act
        chatRoomService.getChatRoomById(roomId);
    }

    @Test
    public void testGetChatRoomByName_ValidName() {
        // Arrange
        String roomName = "Room 1";
        ChatRoom room1 = new ChatRoom();
        room1.setId(1L);
        room1.setName(roomName);
        when(chatRoomRepository.findByName(roomName)).thenReturn(room1);

        // Act
        ChatRoom actualChatRoom = chatRoomService.getChatRoomByName(roomName);

        // Assert
        assertEquals(room1, actualChatRoom);
    }

    @Test(expected = ApplicationException.class)
    public void testGetChatRoomByName_InvalidName() {
        // Arrange
        String roomName = "Non-existent Room";
        when(chatRoomRepository.findByName(roomName)).thenReturn(null);

        // Act
        chatRoomService.getChatRoomByName(roomName);

        // The test should throw an ApplicationException with HttpStatus.NOT_FOUND
    }

    @Test
    public void testGetChatRoomsBySpecificMessage_ValidMessage() {
        // Arrange
        String message = "Hello, everyone!";
        List<ChatRoom> expectedChatRooms = new ArrayList<>();

        ChatRoom room1 = new ChatRoom();
        room1.setId(1L);
        room1.setName("Room 1");

        ChatRoom room2 = new ChatRoom();
        room2.setId(1L);
        room2.setName("Room 1");

        expectedChatRooms.add(room1);
        expectedChatRooms.add(room2);
        when(chatRoomRepository.findBySpecificMessage(message)).thenReturn(expectedChatRooms);

        // Act
        List<ChatRoom> actualChatRooms = chatRoomService.getChatRoomsBySpecificMessage(message);

        // Assert
        assertEquals(expectedChatRooms, actualChatRooms);
    }

    @Test(expected = ApplicationException.class)
    public void testGetChatRoomsBySpecificMessage_InvalidMessage() {
        // Arrange
        String message = "Non-existent message";
        when(chatRoomRepository.findBySpecificMessage(message)).thenReturn(new ArrayList<>());

        // Act
        chatRoomService.getChatRoomsBySpecificMessage(message);
    }

    @Test
    public void testGetChatRoomsBySpecificParticipant_ValidParticipant() {
        // Arrange
        String participant = "User 1";
        List<ChatRoom> expectedChatRooms = new ArrayList<>();

        ChatRoom room1 = new ChatRoom();
        room1.setId(1L);
        room1.setName("Room 1");

        ChatRoom room3 = new ChatRoom();
        room3.setId(3L);
        room3.setName("Room 3");

        expectedChatRooms.add(room1);
        expectedChatRooms.add(room3);
        when(chatRoomRepository.findBySpecificParticipant(participant)).thenReturn(expectedChatRooms);

        // Act
        List<ChatRoom> actualChatRooms = chatRoomService.getChatRoomsBySpecificParticipant(participant);

        // Assert
        assertEquals(expectedChatRooms, actualChatRooms);
    }

    @Test(expected = ApplicationException.class)
    public void testGetChatRoomsBySpecificParticipant_InvalidParticipant() {
        // Arrange
        String participant = "Non-existent participant";
        when(chatRoomRepository.findBySpecificParticipant(participant)).thenReturn(new ArrayList<>());

        // Act
        chatRoomService.getChatRoomsBySpecificParticipant(participant);
    }

    @Test
    public void testCreateChatRoom_ValidData() {
        // Arrange
        String roomName = "Room 1";
        String roomDescription = "Description for Room 1";

        ChatRoom room1 = new ChatRoom();
        room1.setId(1L);
        room1.setName(roomName);

        room1.setDescription(roomDescription);

        when(chatRoomRepository.save(any(ChatRoom.class))).thenReturn(room1);

        // Act
        ChatRoom actualChatRoom = chatRoomService.createChatRoom(roomName, roomDescription);

        // Assert
        assertEquals(room1, actualChatRoom);
    }

    @Test(expected = ApplicationException.class)
    public void testCreateChatRoom_EmptyName() {
        // Act
        chatRoomService.createChatRoom("", "Description for Room 1");

        // The test should throw an ApplicationException with HttpStatus.NOT_FOUND
    }

    @Test(expected = ApplicationException.class)
    public void testCreateChatRoom_EmptyDescription() {
        // Act
        chatRoomService.createChatRoom("Room 1", "");

        // The test should throw an ApplicationException with HttpStatus.NOT_FOUND
    }

    @Test(expected = ApplicationException.class)
    public void testAddParticipant_RoomNotFound() {
        // Arrange
        long roomId = 1L;
        Long userId = 1L;

        when(chatRoomRepository.findById(roomId)).thenReturn(Optional.empty());

        // Act
        chatRoomService.addParticipant(roomId, userId);

        // The test should throw an ApplicationException with HttpStatus.NOT_FOUND
    }

    @Test(expected = ApplicationException.class)
    public void testAddParticipant_UserNotFound() {
        // Arrange
        long roomId = 1L;
        Long userId = 1L;

        when(chatRoomRepository.findById(roomId)).thenReturn(Optional.of(new ChatRoom()));
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act
        chatRoomService.addParticipant(roomId, userId);

        // The test should throw an ApplicationException with HttpStatus.NOT_FOUND
    }

    @Test
    public void testUpdateChatRoom_ValidData() {
        // Arrange
        Long roomId = 1L;
        String roomName = "Updated Room Name";
        String roomDescription = "Updated Room Description";
        ChatRoom existingChatRoom = new ChatRoom();
        existingChatRoom.setId(roomId);
        existingChatRoom.setName("Room 1");

        when(chatRoomRepository.findById(roomId)).thenReturn(Optional.of(existingChatRoom));
        when(chatRoomRepository.save(any(ChatRoom.class))).thenReturn(existingChatRoom);

        // Act
        chatRoomService.updateChatRoom(roomId, roomName, roomDescription);

        // Assert
        assertEquals(roomName, existingChatRoom.getName());
        assertEquals(roomDescription, existingChatRoom.getDescription());
    }

    @Test(expected = ApplicationException.class)
    public void testUpdateChatRoom_RoomNotFound() {
        // Arrange
        Long roomId = 1L;
        String roomName = "Updated Room Name";
        String roomDescription = "Updated Room Description";

        when(chatRoomRepository.findById(roomId)).thenReturn(Optional.empty());

        // Act
        chatRoomService.updateChatRoom(roomId, roomName, roomDescription);

        // The test should throw an ApplicationException with HttpStatus.NOT_FOUND
    }

    @Test(expected = ApplicationException.class)
    public void testUpdateChatRoom_InvalidName() {
        // Arrange
        Long roomId = 1L;
        String roomDescription = "Updated Room Description";
        ChatRoom existingChatRoom = new ChatRoom();
        existingChatRoom.setId(roomId);
        existingChatRoom.setName("Room 1");

        when(chatRoomRepository.findById(roomId)).thenReturn(Optional.of(existingChatRoom));

        // Act
        chatRoomService.updateChatRoom(roomId, null, roomDescription);

        // The test should throw an ApplicationException with HttpStatus.BAD_REQUEST
    }

    @Test(expected = ApplicationException.class)
    public void testUpdateChatRoom_SameName() {
        // Arrange
        Long roomId = 1L;
        String roomName = "Room 1";
        String roomDescription = "Updated Room Description";
        ChatRoom existingChatRoom = new ChatRoom();
        existingChatRoom.setId(roomId);
        existingChatRoom.setName(roomName);

        when(chatRoomRepository.findById(roomId)).thenReturn(Optional.of(existingChatRoom));

        // Act
        chatRoomService.updateChatRoom(roomId, roomName, roomDescription);

        // The test should throw an ApplicationException with HttpStatus.BAD_REQUEST
    }

    @Test(expected = ApplicationException.class)
    public void testUpdateChatRoom_InvalidDescription() {
        // Arrange
        Long roomId = 1L;
        String roomName = "Updated Room Name";
        ChatRoom existingChatRoom = new ChatRoom();
        existingChatRoom.setId(roomId);
        existingChatRoom.setName(roomName);

        when(chatRoomRepository.findById(roomId)).thenReturn(Optional.of(existingChatRoom));

        // Act
        chatRoomService.updateChatRoom(roomId, roomName, null);

        // The test should throw an ApplicationException with HttpStatus.BAD_REQUEST
    }

    @Test(expected = ApplicationException.class)
    public void testUpdateChatRoom_SameDescription() {
        // Arrange
        Long roomId = 1L;
        String roomName = "Updated Room Name";
        String roomDescription = "Room 1 Description";
        ChatRoom existingChatRoom = new ChatRoom();
        existingChatRoom.setId(roomId);
        existingChatRoom.setName(roomName);
        existingChatRoom.setDescription(roomDescription);

        when(chatRoomRepository.findById(roomId)).thenReturn(Optional.of(existingChatRoom));

        // Act
        chatRoomService.updateChatRoom(roomId, roomName, roomDescription);
    }

    @Test
    public void testDeleteChatRoom_ValidId() {
        // Arrange
        Long roomId = 1L;

        ChatRoom existingChatRoom = new ChatRoom();
        existingChatRoom.setId(roomId);
        existingChatRoom.setName("Room 1");

        when(chatRoomRepository.findById(roomId)).thenReturn(Optional.of(existingChatRoom));

        // Act
        chatRoomService.deleteChatRoom(roomId);

        // Assert
        verify(chatRoomRepository, times(1)).delete(existingChatRoom);
    }

    @Test(expected = ApplicationException.class)
    public void testDeleteChatRoom_InvalidId() {
        // Arrange
        Long roomId = 1L;
        when(chatRoomRepository.findById(roomId)).thenReturn(Optional.empty());

        // Act
        chatRoomService.deleteChatRoom(roomId);

        // The test should throw an ApplicationException with HttpStatus.NOT_FOUND
    }

    @Test(expected = ApplicationException.class)
    public void testDeleteParticipant_RoomNotFound() {
        // Arrange
        long roomId = 1L;
        long userId = 1L;

        when(chatRoomRepository.findById(roomId)).thenReturn(Optional.empty());

        // Act
        chatRoomService.deleteParticipant(roomId, userId);
    }

    @Test(expected = ApplicationException.class)
    public void testDeleteParticipant_UserNotFound() {
        // Arrange
        long roomId = 1L;
        long userId = 1L;

        when(chatRoomRepository.findById(roomId)).thenReturn(Optional.of(new ChatRoom()));
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act
        chatRoomService.deleteParticipant(roomId, userId);
    }
}

