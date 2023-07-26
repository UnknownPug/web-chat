package com.example.webchat.repository;

import com.example.webchat.entity.ChatRoom;
import com.example.webchat.entity.Message;
import com.example.webchat.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    ChatRoom findByName(String name);

    @Query(value = "SELECT c FROM ChatRoom AS c JOIN c.participants AS p WHERE p.username = :name")
    ChatRoom findByUserName(@Param("name") String name);

    @Query(value = "SELECT DISTINCT c FROM ChatRoom AS c JOIN c.message AS m WHERE m IN :messages")
    List<ChatRoom> findChatRoomsByMessagesIn(@Param("messages") List<Message> messages);

    @Query(value = "SELECT c FROM ChatRoom AS c WHERE c.participants IN :users")
    List<ChatRoom> findByParticipants(@Param("users") List<User> users);
}
