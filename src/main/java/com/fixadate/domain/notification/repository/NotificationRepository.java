package com.fixadate.domain.notification.repository;

import com.fixadate.domain.notification.entity.Notification;
import com.fixadate.domain.notification.enumerations.PushNotificationType;
import com.fixadate.domain.pushkey.entity.PushKey;
import io.lettuce.core.dynamic.annotation.Param;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    Page<Notification> findAllByMemberId(String memberId, Pageable pageable);
    @Query("SELECT n FROM Notification n WHERE n.isRead = false AND n.isReSend = false AND n.eventType IN :eventTypes AND n.createDate < :time")
    List<Notification> findUnReadNotificationsBeforeWithEventTypes(@Param("time") LocalDateTime time,
        @Param("eventTypes") List<PushNotificationType> eventTypes);


}
