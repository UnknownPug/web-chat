package app.nss.webchat.repository;

import app.nss.webchat.entity.Notification;
import app.nss.webchat.entity.NotificationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByRecipientIdAndStatus(Long recipientId, NotificationStatus status);

    @Modifying
    @Query("UPDATE Notification AS n SET n.status = :newStatus WHERE n.status = :oldStatus AND n.recipient.id = :recipientId")
    void updateStatus(@Param("newStatus") NotificationStatus newStatus,
                      @Param("oldStatus") NotificationStatus oldStatus,
                      @Param("recipientId") Long recipientId);


}
