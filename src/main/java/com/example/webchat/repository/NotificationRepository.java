package com.example.webchat.repository;

import com.example.webchat.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findAllByStatusIsFalse();

    List<Notification> findAllByStatusIsTrue();

    @Modifying
    @Query(value = "UPDATE notification SET status = true WHERE status = false", nativeQuery = true)
    void updateStatusFromFalseToTrue();

}
