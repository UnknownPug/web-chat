package app.nss.webchat.repository;

import app.nss.webchat.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    ChatRoom findByName(String name);

    List<ChatRoom> findAllByParticipantsUsername(String name);

    @Query(value = "SELECT c FROM ChatRoom AS c JOIN c.participants AS p WHERE p.username = :name")
    ChatRoom findByUserName(@Param("name") String name);

    @Query(value = "SELECT c FROM ChatRoom AS c JOIN c.participants AS p WHERE p.username = :participant")
    List<ChatRoom> findBySpecificParticipant(@Param("participant") String participant);

    @Query(value = "SELECT c FROM ChatRoom AS c JOIN c.messages AS m WHERE m.content = :message")
    List<ChatRoom> findBySpecificMessage(@Param("message") String message);


}
