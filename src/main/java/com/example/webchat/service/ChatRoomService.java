package com.example.webchat.service;

import com.example.webchat.entity.ChatRoom;
import com.example.webchat.entity.Message;
import com.example.webchat.entity.User;
import com.example.webchat.exception.ApplicationException;
import com.example.webchat.repository.ChatRoomRepository;
import com.example.webchat.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;

    @Autowired
    public ChatRoomService(ChatRoomRepository chatRoomRepository,
                           UserRepository userRepository) {
        this.chatRoomRepository = chatRoomRepository;
        this.userRepository = userRepository;
    }

    public List<ChatRoom> getChatRooms() {
        return chatRoomRepository.findAll();
    }

    public ChatRoom getChatRoomById(long id) {
        return chatRoomRepository.findById(id).orElseThrow(
                () -> new ApplicationException(HttpStatus.NOT_FOUND, "Room with id " + id + " not found.")
        );
    }

    public ChatRoom getChatRoomByName(String name) {
        ChatRoom room = chatRoomRepository.findByName(name);
        if (room == null) {
            throw new ApplicationException(HttpStatus.NOT_FOUND, "Room with name " + name + " not found.");
        }
        return room;
    }

    public ChatRoom getChatRoomByUser(String name) {
        ChatRoom room = chatRoomRepository.findByUserName(name);
        if (room == null) {
            throw new ApplicationException(HttpStatus.NOT_FOUND, "User with name " + name + " not found.");
        }
        return room;
    }

    public List<ChatRoom> getChatRoomBySpecificMessages(List<Message> message) {
        List<ChatRoom> room = chatRoomRepository.findChatRoomsByMessagesIn(message);
        if (room.isEmpty()) {
            throw new ApplicationException(HttpStatus.NOT_FOUND, "Room with this specific message not found.");
        }
        return room;
    }

    public List<ChatRoom> getChatRoomBySpecificParticipants(List<User> participants) {
        List<ChatRoom> room = chatRoomRepository.findByParticipants(participants);
        if (room.isEmpty()) {
            throw new ApplicationException(HttpStatus.NOT_FOUND, "Room with this participants not found.");
        }
        return room;
    }

    public ChatRoom createChatRoom(String name, String description) {
        ChatRoom room = new ChatRoom();
        if (name.isEmpty() || description.isEmpty()) {
            throw new ApplicationException(HttpStatus.NOT_FOUND, "Room must contain name and description.");
        }
        room.setName(name);
        room.setDescription(description);
        return chatRoomRepository.save(room);
    }

    public void addParticipant(long id, Long userId) {
        ChatRoom room = chatRoomRepository.findById(id).orElseThrow(
                () -> new ApplicationException(HttpStatus.NOT_FOUND, "Room with id " + id + " not found.")
        );
        User user = userRepository.findById(userId).orElseThrow(
                () -> new ApplicationException(HttpStatus.NOT_FOUND, "User with id " + id + " not found.")
        );
        if (room.getUsers().contains(user)) {
            throw new ApplicationException(HttpStatus.BAD_REQUEST, "User is already in this room.");
        }
        room.getUsers().add(user);
        chatRoomRepository.save(room);
    }

    public void updateChatRoom(Long id, String name, String description) {
        ChatRoom room = chatRoomRepository.findById(id).orElseThrow(
                () -> new ApplicationException(HttpStatus.BAD_REQUEST, "Room with id " + id + " not found.")
        );
        room.setName(name);
        room.setDescription(description);
        chatRoomRepository.save(room);
    }

    public void deleteChatRoom(Long id) {
        ChatRoom room = chatRoomRepository.findById(id).orElseThrow(
                () -> new ApplicationException(HttpStatus.BAD_REQUEST, "Room with id " + id + " not found.")
        );
        chatRoomRepository.delete(room);
    }

    public void deleteParticipant(long id, long userId) {
        ChatRoom room = chatRoomRepository.findById(id).orElseThrow(
                () -> new ApplicationException(HttpStatus.NOT_FOUND, "Room with id " + id + " not found.")
        );
        User user = userRepository.findById(userId).orElseThrow(
                () -> new ApplicationException(HttpStatus.NOT_FOUND, "User with id " + id + " not found.")
        );
        if (!room.getUsers().contains(user)) {
            throw new ApplicationException(HttpStatus.BAD_REQUEST, "User is not found in this room.");
        }
        room.getUsers().remove(user);
        chatRoomRepository.save(room);
    }
}
