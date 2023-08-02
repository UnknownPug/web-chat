package app.nss.webchat.repository;

import app.nss.webchat.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    List<Message> findAllByIdOrderByTimeStampAsc(Long userId);

    List<Message> findAllByIdOrderByTimeStampDesc(Long id);

    @Query(value = "SELECT m FROM Message AS m WHERE m.room.id = :id ORDER BY m.timeStamp ASC")
    List<Message> findAllForChatWithIdOrderByTimeStampAsc(Long id);

    List<Message> findAllByTimeStamp(LocalDateTime time);

    @Query("SELECT m FROM Message AS m WHERE LOWER(m.content) LIKE %:keyword%")
    List<Message> findAllMessagesByKeyword(String keyword);
}
