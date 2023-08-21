package app.nss.webchat.repository;

import app.nss.webchat.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    List<Message> findAllBySenderIdOrderByTimeStampAsc(Long userId);

    List<Message> findAllBySenderIdOrderByTimeStampDesc(Long userId);


    @Query(value = "SELECT m FROM Message AS m WHERE m.room.id = :id ORDER BY m.timeStamp ASC")
    List<Message> findAllForChatWithIdOrderByTimeStampAsc(Long id);

    @Query("SELECT m FROM Message AS m WHERE LOWER(m.content) LIKE %:keyword%")
    List<Message> findAllMessagesByKeyword(@Param("keyword") String keyword);

    @Query("SELECT CASE WHEN COUNT(m) > 0 THEN TRUE ELSE FALSE END FROM Message m WHERE m.id = :messageId AND m.sender.id = :senderId")
    boolean existsMessageByIdAndSenderId(@Param("messageId") Long messageId, @Param("senderId") Long senderId);
}
