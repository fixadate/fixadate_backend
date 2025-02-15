package com.fixadate.domain.notification.repository;

import com.fixadate.domain.notification.entity.Notification;
import com.fixadate.domain.pushkey.entity.PushKey;
import java.util.List;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    Page<Notification> findAllByMemberId(String memberId, Pageable pageable);
}
