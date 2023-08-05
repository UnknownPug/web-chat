package app.nss.webchat.service;

import app.nss.webchat.entity.ChatRoom;
import app.nss.webchat.entity.Message;
import app.nss.webchat.entity.User;
import app.nss.webchat.exception.ApplicationException;
import app.nss.webchat.repository.ChatRoomRepository;
import app.nss.webchat.repository.MessageRepository;
import app.nss.webchat.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
public class MessageService {

    private final MessageRepository messageRepository;
    private final PaginationService paginationService;
    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;

    @Autowired
    public MessageService(MessageRepository messageRepository, PaginationService paginationService,
                          ChatRoomRepository chatRoomRepository,
                          UserRepository userRepository) {
        this.messageRepository = messageRepository;
        this.paginationService = paginationService;
        this.chatRoomRepository = chatRoomRepository;
        this.userRepository = userRepository;
    }

    @Cacheable(value = "messagesCache", key = "{#root.methodName, #root.args}")
    public List<Message> getAllMessages() {
        return messageRepository.findAll();
    }

    @Cacheable(value = "messagesCache", key = "{#root.methodName, #root.args}")
    public Message getMessageById(Long id) {
        return messageRepository.findById(id).orElseThrow(
                () -> new ApplicationException(HttpStatus.NOT_FOUND, "Message with id " + id + " not found."
                ));
    }

    @Cacheable(value = "messagesCache", key = "{#root.methodName, #root.args}")
    public List<Message> getSortedMessagesForUserAsc(Long id) {
        return messageRepository.findAllBySenderIdOrderByTimeStampAsc(id);
    }

    @Cacheable(value = "messagesCache", key = "{#root.methodName, #root.args}")
    public List<Message> getSortedMessagesForUserDesc(Long id) {
        return messageRepository.findAllBySenderIdOrderByTimeStampDesc(id);
    }

    @Cacheable(value = "messagesCache", key = "{#root.methodName, #root.args}")
    public List<Message> getSortedMessagesForChat(Long id) {
        return messageRepository.findAllForChatWithIdOrderByTimeStampAsc(id);
    }

    @Cacheable(value = "messagesCache", key = "{#root.methodName, #root.args}")
    public List<Message> getSortedMessagesByKeyword(String keyword) {
        String trimmedKeyword = keyword.trim().toLowerCase();
        return messageRepository.findAllMessagesByKeyword(trimmedKeyword);
    }

    @Cacheable(value = "messagesCache", key = "{#root.methodName, #root.args}")
    public List<Message> getSortedMessages(int limit, int offset) {
        List<Message> allMessages = messageRepository.findAll();
        return paginationService.getPaginatedList(allMessages, limit, offset);
    }

    @CachePut(value = "messagesCache", key = "#result.id")
    public Message sendMessage(String content, Long roomId, Long senderId) {
        Message message = new Message();
        ChatRoom room = chatRoomRepository.findById(roomId).orElseThrow(
                () -> new ApplicationException(HttpStatus.NOT_FOUND, "Room with id " + roomId + " not found.")
        );
        User sender = userRepository.findById(senderId).orElseThrow(
                () -> new ApplicationException(HttpStatus.NOT_FOUND, "Sender with id " + senderId + " not found.")
        );
        if (content.isEmpty()) {
            throw new ApplicationException(HttpStatus.BAD_REQUEST, "Message must contain a text.");
        }
        message.setContent(content);
        message.setRoom(room);
        message.setSender(sender);
        message.setTimeStamp(LocalDateTime.now());
        return messageRepository.save(message);
    }

    @CacheEvict(value = "messagesCache", key = "#id")
    @CachePut(value = "messagesCache", key = "#id")
    public void updateMessage(Long id, String content) {
        Message message = messageRepository.findById(id).orElseThrow(
                () -> new ApplicationException(HttpStatus.NOT_FOUND, "Message with id " + id + " not found.")
        );
        if (content != null && !content.isEmpty() && !Objects.equals(message.getContent(), content)) {
            message.setContent(content);
        } else {
            throw new ApplicationException(HttpStatus.BAD_REQUEST,
                    "Message content must be defined or should not be the same as the existing message.");
        }
        messageRepository.save(message);
    }

    @CacheEvict(value = "messagesCache", key = "#id")
    public void deleteMessage(Long id) {
        Message message = messageRepository.findById(id).orElseThrow(
                () -> new ApplicationException(HttpStatus.NOT_FOUND, "Message not found.")
        );
        messageRepository.delete(message);
    }
}
