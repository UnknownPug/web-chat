package app.nss.webchat.service;

import app.nss.webchat.entity.ChatRoom;
import app.nss.webchat.entity.Message;
import app.nss.webchat.entity.User;
import app.nss.webchat.exception.ApplicationException;
import app.nss.webchat.repository.ChatRoomRepository;
import app.nss.webchat.repository.MessageRepository;
import app.nss.webchat.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@SpringBootTest
public class MessageServiceTest {

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private ChatRoomRepository chatRoomRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private MessageService messageService;

    @Test
    public void testGetAllMessages() {
        // Mocking the behavior of messageRepository.findAll()
        List<Message> mockMessages = new ArrayList<>();

        Message message1 = new Message();
        message1.setId(1L);
        message1.setContent("Hello");

        Message message2 = new Message();

        message2.setId(2L);
        message2.setContent("Hi");

        mockMessages.add(message1);
        mockMessages.add(message2);
        when(messageRepository.findAll()).thenReturn(mockMessages);

        List<Message> messages = messageService.getAllMessages();

        // Assertions
        assertEquals(mockMessages.size(), messages.size());
        assertEquals(mockMessages.get(0).getContent(), messages.get(0).getContent());
        assertEquals(mockMessages.get(1).getId(), messages.get(1).getId());
    }

    @Test
    public void testGetMessageById() {
        // Mocking the behavior of messageRepository.findById()
        Long messageId = 1L;

        Message mockMessage = new Message();
        mockMessage.setId(1L);
        mockMessage.setContent("Hello");

        when(messageRepository.findById(anyLong())).thenReturn(Optional.of(mockMessage));

        // Test the successful case
        Message message = messageService.getMessageById(messageId);

        // Assertions
        assertEquals(messageId, message.getId());
        assertEquals("Hello", message.getContent());

        // Test the case where the message with the given id is not found
        when(messageRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Assertions
        assertThrows(ApplicationException.class, () -> messageService.getMessageById(messageId));
    }

    @Test
    public void testGetSortedMessagesForUserAsc() {
        Long userId = 1L;

        // Mocking the behavior of messageRepository.findAllByIdOrderByTimeStampAsc()
        List<Message> mockMessages = getMessages(userId);
        when(messageRepository.findAllBySenderIdOrderByTimeStampAsc(userId)).thenReturn(mockMessages);

        List<Message> messages = messageService.getSortedMessagesForUserAsc(userId);

        // Assertions
        assertEquals(mockMessages.size(), messages.size());
        assertEquals(mockMessages.get(0).getContent(), messages.get(0).getContent());
        assertEquals(mockMessages.get(1).getId(), messages.get(1).getId());
    }

    private static List<Message> getMessages(Long userId) {
        List<Message> mockMessages = new ArrayList<>();
        User user = new User();
        Message message1 = new Message();
        Message message2 = new Message();

        user.setId(userId);

        message1.setId(1L);
        message1.setContent("Hello");
        message1.setSender(user);

        message2.setId(2L);
        message2.setContent("Hi");
        message2.setSender(user);

        mockMessages.add(message1);
        mockMessages.add(message2);
        return mockMessages;
    }

    @Test
    public void testGetSortedMessagesForUserDesc() {
        Long userId = 1L;

        // Mocking the behavior of messageRepository.findAllByIdOrderByTimeStampDesc()
        List<Message> mockMessages = getMessages(userId);

        when(messageRepository.findAllBySenderIdOrderByTimeStampDesc(userId)).thenReturn(mockMessages);

        List<Message> messages = messageService.getSortedMessagesForUserDesc(userId);

        // Assertions
        assertEquals(mockMessages.size(), messages.size());
        assertEquals(mockMessages.get(0).getContent(), messages.get(0).getContent());
        assertEquals(mockMessages.get(1).getId(), messages.get(1).getId());
    }

    @Test
    public void testGetSortedMessagesForChat() {
        Long chatId = 1L;

        // Mocking the behavior of messageRepository.findAllForChatWithIdOrderByTimeStampAsc()
        List<Message> mockMessages = getMessageList(chatId);
        when(messageRepository.findAllForChatWithIdOrderByTimeStampAsc(chatId)).thenReturn(mockMessages);

        List<Message> messages = messageService.getSortedMessagesForChat(chatId);

        // Assertions
        assertEquals(mockMessages.size(), messages.size());
        assertEquals(mockMessages.get(0).getContent(), messages.get(0).getContent());
        assertEquals(mockMessages.get(1).getId(), messages.get(1).getId());
    }

    private static List<Message> getMessageList(Long chatId) {
        List<Message> mockMessages = new ArrayList<>();
        ChatRoom room = new ChatRoom();
        Message message1 = new Message();
        Message message2 = new Message();

        room.setId(chatId);

        message1.setId(1L);
        message1.setContent("Hello");
        message1.setRoom(room);

        message2.setId(2L);
        message2.setContent("Hi");
        message2.setRoom(room);

        mockMessages.add(message1);
        mockMessages.add(message2);
        return mockMessages;
    }

    @Test
    public void testSendMessage() {
        String content = "Hello";
        Long roomId = 1L;
        Long senderId = 1L;

        // Mocking the behavior of chatRoomRepository.findById()
        ChatRoom mockRoom = new ChatRoom();
        mockRoom.setId(roomId);
        mockRoom.setName("Room 1");
        when(chatRoomRepository.findById(roomId)).thenReturn(java.util.Optional.of(mockRoom));

        // Mocking the behavior of userRepository.findById()
        User mockSender = new User();
        mockSender.setId(senderId);
        mockSender.setUsername("John Doe");
        when(userRepository.findById(senderId)).thenReturn(java.util.Optional.of(mockSender));

        // Mocking the behavior of messageRepository.save()
        Message mockMessage = new Message();
        mockMessage.setId(1L);
        mockMessage.setContent(content);
        mockMessage.setRoom(mockRoom);
        mockMessage.setSender(mockSender);
        when(messageRepository.save(any(Message.class))).thenReturn(mockMessage);

        Message message = messageService.sendMessage(content, roomId, senderId);

        // Assertions
        assertEquals(content, message.getContent());
        assertEquals(roomId, message.getRoom().getId());
        assertEquals(senderId, message.getSender().getId());
    }

    @Test
    public void testSendMessageWithEmptyContent() {
        String content = ""; // Empty content
        Long roomId = 1L;
        Long senderId = 1L;

        // Mocking the behavior of chatRoomRepository.findById()
        ChatRoom mockRoom = new ChatRoom();
        mockRoom.setId(roomId);
        mockRoom.setName("Room 1");
        when(chatRoomRepository.findById(roomId)).thenReturn(java.util.Optional.of(mockRoom));

        // Mocking the behavior of userRepository.findById()
        User mockSender = new User();
        mockSender.setId(senderId);
        mockSender.setUsername("John Doe");
        when(userRepository.findById(senderId)).thenReturn(java.util.Optional.of(mockSender));

        // Assertions
        assertThrows(ApplicationException.class, () -> messageService.sendMessage(content, roomId, senderId));
    }

    @Test
    public void testUpdateMessageWithNoChange() {
        Long messageId = 1L;
        String sameContent = "Same content";

        // Mocking the behavior of messageRepository.findById()
        Message mockMessage = new Message();
        mockMessage.setId(messageId);
        mockMessage.setContent(sameContent);
        when(messageRepository.findById(messageId)).thenReturn(Optional.of(mockMessage));

        // Assertions
        assertThrows(ApplicationException.class, () -> messageService.updateMessage(messageId, sameContent));
    }

    @Test
    public void testUpdateMessageNotFound() {
        Long messageId = 1L;

        // Mocking the behavior of messageRepository.findById()
        when(messageRepository.findById(messageId)).thenReturn(Optional.empty());

        // Assertions
        assertThrows(ApplicationException.class, () -> messageService.updateMessage(messageId, "Updated content"));
    }

    @Test
    public void testDeleteMessage() {
        Long messageId = 1L;

        // Mocking the behavior of messageRepository.findById()
        Message mockMessage = new Message();
        mockMessage.setId(messageId);
        mockMessage.setContent("Content");
        when(messageRepository.findById(messageId)).thenReturn(Optional.of(mockMessage));

        messageService.deleteMessage(messageId);

        // Assertions
        verify(messageRepository).delete(mockMessage);
    }

    @Test
    public void testDeleteMessageNotFound() {
        Long messageId = 1L;

        // Mocking the behavior of messageRepository.findById()
        when(messageRepository.findById(messageId)).thenReturn(Optional.empty());

        // Assertions
        assertThrows(ApplicationException.class, () -> messageService.deleteMessage(messageId));
    }
}

