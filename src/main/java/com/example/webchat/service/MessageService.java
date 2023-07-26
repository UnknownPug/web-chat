package com.example.webchat.service;

import com.example.webchat.entity.Message;
import com.example.webchat.exception.ApplicationException;
import com.example.webchat.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MessageService {

    private final MessageRepository messageRepository;
    private final PaginationService paginationService;

    @Autowired
    public MessageService(MessageRepository messageRepository, PaginationService paginationService) {
        this.messageRepository = messageRepository;
        this.paginationService = paginationService;
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
        return messageRepository.findMessagesByKeyword(keyword);
    }

    public List<Message> getSortedMessages(int limit, int offset) {
        List<Message> allMessages = messageRepository.findAll();
        return paginationService.getPaginatedList(allMessages, limit, offset);
    }

    public Message createMessage(String content) {
        Message message = new Message();
        message.setContent(content);
        return messageRepository.save(message);
    }

    public void updateMessage(Long id, String content) {
        Message message = messageRepository.findById(id).orElseThrow(
                () -> new ApplicationException(HttpStatus.NOT_FOUND, "Message with id " + id + " not found.")
        );
        if (content.isEmpty()) {
            throw new ApplicationException(HttpStatus.BAD_REQUEST, "Message content must be specified.");
        }
        message.setContent(content);
    }

    public void deleteMessage(Long id) {
        Message message = messageRepository.findById(id).orElseThrow(
                () -> new ApplicationException(HttpStatus.NOT_FOUND, "Message not found.")
        );
        messageRepository.delete(message);
    }
}
