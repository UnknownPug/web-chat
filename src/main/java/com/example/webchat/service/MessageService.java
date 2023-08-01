package com.example.webchat.service;

import com.example.webchat.entity.ChatRoom;
import com.example.webchat.entity.Message;
import com.example.webchat.entity.User;
import com.example.webchat.exception.ApplicationException;
import com.example.webchat.repository.ChatRoomRepository;
import com.example.webchat.repository.MessageRepository;
import com.example.webchat.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
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

    public List<Message> getAllMessages() {
        return messageRepository.findAll();
    }

    public Message getMessageById(Long id) {
        return messageRepository.findById(id).orElseThrow(
                () -> new ApplicationException(HttpStatus.NOT_FOUND, "Message with id " + id + " not found."
                ));
    }

    public List<Message> getSortedMessagesForUserAsc(Long id) {
        return messageRepository.findAllByIdOrderByTimeStampAsc(id);
    }

    public List<Message> getSortedMessagesForUserDesc(Long id) {
        return messageRepository.findAllByIdOrderByTimeStampDesc(id);
    }

    public List<Message> getSortedMessagesForChat(Long id) {
        return messageRepository.findAllForChatWithIdOrderByTimeStampAsc(id);
    }

    public List<Message> getSortedMessagesByTimeStamp(LocalDateTime timestamp) {
        return messageRepository.findAllByTimeStamp(timestamp);
    }

    public List<Message> getSortedMessagesByKeyword(String keyword) {
        return messageRepository.findAllMessagesByKeyword(keyword);
    }

    public List<Message> getSortedMessages(int limit, int offset) {
        List<Message> allMessages = messageRepository.findAll();
        return paginationService.getPaginatedList(allMessages, limit, offset);
    }

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
        return messageRepository.save(message);
    }

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
    }

    public void deleteMessage(Long id) {
        Message message = messageRepository.findById(id).orElseThrow(
                () -> new ApplicationException(HttpStatus.NOT_FOUND, "Message not found.")
        );
        messageRepository.delete(message);
    }
}
