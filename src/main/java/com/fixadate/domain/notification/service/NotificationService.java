package com.fixadate.domain.notification.service;

import com.fixadate.domain.member.entity.Member;
import com.fixadate.domain.notification.dto.NotificationListResponse;
import com.fixadate.domain.notification.dto.NotificationPageResponse;
import com.fixadate.domain.notification.entity.Notification;
import com.fixadate.domain.notification.repository.NotificationRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
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

    public NotificationPageResponse getNotificationList(Member member,
        Pageable pageable) {
        Page<Notification> foundAlarmList = notificationRepository.findAllByMemberId(member.getId(), pageable);
        List<NotificationListResponse> alarmListResponse = foundAlarmList.stream()
            .map(NotificationListResponse::new).collect(
                Collectors.toList());

        return new NotificationPageResponse(new PageImpl<>(alarmListResponse, pageable, foundAlarmList.getTotalElements()));
    }

    public boolean changeNotificationRead(Long id, Member member) {
        Notification foundNotification = findNotificationWithMember(id, member);
        foundNotification.read();
        return foundNotification.isRead();
    }

    public boolean deleteNotification(Long id, Member member) {
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
