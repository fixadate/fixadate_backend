package com.fixadate.domain.notification.service;

import com.fixadate.domain.member.entity.Member;
import com.fixadate.domain.notification.dto.FirebaseCloudMessageService;
import com.fixadate.domain.notification.dto.NotificationListResponse;
import com.fixadate.domain.notification.dto.NotificationPageResponse;
import com.fixadate.domain.notification.entity.Notification;
import com.fixadate.domain.notification.enumerations.PushNotificationType;
import com.fixadate.domain.notification.repository.NotificationRepository;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = {Exception.class, RuntimeException.class})
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final FirebaseCloudMessageService firebaseCloudMessageService;

    public NotificationPageResponse getNotificationList(Member member,
        Pageable pageable) {
        Page<Notification> foundNotificationList = notificationRepository.findAllByMemberId(member.getId(), pageable);
        List<NotificationListResponse> notificationListResponse = foundNotificationList.stream()
            .map(NotificationListResponse::new).collect(
                Collectors.toList());

        return new NotificationPageResponse(new PageImpl<>(notificationListResponse, pageable, foundNotificationList.getTotalElements()));
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

    @Scheduled(fixedRate = 600000) // 10분마다 실행
    public void resendUnconfirmedNotifications() throws IOException {
        List<PushNotificationType> eventTypes = Arrays.asList(
            PushNotificationType.DATES_MARK_REQUEST,
            PushNotificationType.DATES_CHOICE
        );
        LocalDateTime threeHoursAgo = LocalDateTime.now().minusHours(3);
        List<Notification> unconfirmedNotifications = notificationRepository.findUnReadNotificationsBeforeWithEventTypes(threeHoursAgo, eventTypes);

        for (Notification notification : unconfirmedNotifications) {
            String targetToken = notification.getPushKey();
            String title = notification.getTitle();
            String content = notification.getContent();

            Map<String, Object> data = Map.of(
                "eventType", notification.getEventType().name(),
                "valueObject", notification.createValueObj(),
                "image", notification.getImage()
            );
            firebaseCloudMessageService.sendMessageToWithData(targetToken, title, content, data);
        }
    }
}
