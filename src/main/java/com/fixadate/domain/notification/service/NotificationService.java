package com.fixadate.domain.notification.service;

import com.fixadate.domain.auth.service.AuthService;
import com.fixadate.domain.member.entity.Member;
import com.fixadate.domain.notification.dto.NotificationListResponse;
import com.fixadate.domain.notification.entity.Notification;
import com.fixadate.domain.notification.event.object.AlarmCheckEvent;
import com.fixadate.domain.notification.event.object.TeamDeleteEvent;
import com.fixadate.domain.notification.repository.NotificationRepository;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = {Exception.class, RuntimeException.class})
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public Page<NotificationListResponse> getNotificationList(Member member,
        Pageable pageable) {
        Page<Notification> foundAlarmList = notificationRepository.findAllByMemberId(member.getId(), pageable);
        List<NotificationListResponse> alarmListResponse = foundAlarmList.stream()
            .map(NotificationListResponse::new).collect(
                Collectors.toList());

        return new PageImpl<>(alarmListResponse, pageable, foundAlarmList.getTotalElements());
    }

    public boolean changeAlarmRead(Long id, Member member) {
        Notification foundNotification = findNotificationWithMember(id, member);
        foundNotification.read();
        return foundNotification.isRead();
    }

    public boolean deleteAlarm(Long id, Member member) {
        Notification foundNotification = findNotificationWithMember(id, member);
        Long notificationId = foundNotification.getId();
        notificationRepository.deleteById(foundNotification.getId());
        return !notificationRepository.existsById(notificationId);
    }

    public Notification findNotificationWithMember(Long id, Member member) {
        Notification foundNotification = notificationRepository.findById(id).orElseThrow(
            () -> new RuntimeException("Notification not found")
        );
        if(!foundNotification.getMemberId().equals(member.getId())) {
            throw new RuntimeException("invalid access to notification");
        }
        return foundNotification;
    }
}
