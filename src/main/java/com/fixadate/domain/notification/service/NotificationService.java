package com.fixadate.domain.notification.service;

import com.fixadate.domain.dates.dto.DatesCoordinationDto;
import com.fixadate.domain.dates.entity.DatesCoordinations;
import com.fixadate.domain.dates.repository.DatesCoordinationsRepository;
import com.fixadate.domain.member.entity.Member;
import com.fixadate.domain.notification.dto.FirebaseCloudMessageService;
import com.fixadate.domain.notification.dto.NotificationListResponse;
import com.fixadate.domain.notification.dto.NotificationPageResponse;
import com.fixadate.domain.notification.entity.Notification;
import com.fixadate.domain.notification.enumerations.PushNotificationType;
import com.fixadate.domain.notification.repository.EmitterRepository;
import com.fixadate.domain.notification.repository.NotificationRepository;
import com.fixadate.global.exception.ExceptionCode;
import com.fixadate.global.util.TimeUtil;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = {Exception.class, RuntimeException.class})
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final FirebaseCloudMessageService firebaseCloudMessageService;
    private final EmitterRepository emitterRepository;
    private final DatesCoordinationsRepository datesCoordinationsRepository;
    private final static Long SSE_DEFAULT_TIMEOUT = 3600000L; //1시간
    private final String NOTIFICATION_NAME = "alive_notification";

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

    @Scheduled(fixedRate = 60000) // 1분마다 실행
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

    public SseEmitter connectNotification(String userId) {
        // 새로운 SseEmitter를 만든다
        SseEmitter sseEmitter = new SseEmitter(SSE_DEFAULT_TIMEOUT);

        // 유저 ID로 SseEmitter를 저장한다.
        emitterRepository.save(userId, sseEmitter);

        // 세션이 종료될 경우 저장한 SseEmitter를 삭제한다.
        sseEmitter.onCompletion(() -> emitterRepository.delete(userId));
        sseEmitter.onTimeout(() -> emitterRepository.delete(userId));

        // 503 Service Unavailable 오류가 발생하지 않도록 첫 데이터를 보낸다.
        // 새로운 연결을 생성할 때에는 유저의 ID를 받아 SSE Emitter를 리포지토리에 저장.
        // 이후, SSE 응답을 할 때 아무런 이벤트도 보내지 않으면 재연결 요청을 보낼때나, 아니면 연결 요청 자체에서 오류가 발생하기 때문에, 첫 응답을 보내야한다.
        try {
            sseEmitter.send(SseEmitter.event().id("").name(NOTIFICATION_NAME).data("Connection completed"));
        } catch (IOException exception) {
            throw new RuntimeException(ExceptionCode.NOTIFICATION_CONNECTION_ERROR.getMessage());
        }
        return sseEmitter;
    }

    // 알림이 발생할 때마다 아래 메서드를 호출하도록 구현했다.
    public void sendEvent(String userId) {
        // 유저 ID로 SseEmitter를 찾아 이벤트를 발생 시킨다.
        emitterRepository.get(userId).ifPresentOrElse(sseEmitter -> {
            try {
                // SseEmitter에 이벤트를 발생시킨다. 다만, 실시간으로 확인만 하라고하면 되니 데이터 최소화
                sseEmitter.send(SseEmitter.event().id("").name(NOTIFICATION_NAME).data("New notification"));
            } catch (IOException exception) {
                // IOException이 발생하면 저장된 SseEmitter를 삭제하고 예외를 발생시킨다.
                emitterRepository.delete(userId);
                throw new RuntimeException(ExceptionCode.NOTIFICATION_CONNECTION_ERROR.getMessage());
            }
        }, () -> log.info("No emitter found"));
    }

    @Transactional
    public void sendDatesCoordinationCreateEvent(List<Member> members, DatesCoordinationDto datesCoordinationDto) {
        for (Member member : members) {
            Notification notification = Notification.builder()
                .member(member)
                .pushKey(member.getPushKey().getPushKey())
                .title("팀 일정 가능 시간 표기 요청")
                .content(datesCoordinationDto.title()+"("+ TimeUtil.convertMinutesToTime(datesCoordinationDto.minutes()) + "소요)")
                .eventType(PushNotificationType.DATES_MARK_REQUEST)
                .value(String.valueOf(datesCoordinationDto.id()))
                .image("")
                .build();
            notificationRepository.save(notification);
            Map<String, Object> data = Map.of(
                "id", notification.getValue()
            );
            try {
                firebaseCloudMessageService.sendMessageToWithData(member.getPushKey().getPushKey(),
                    notification.getTitle(), notification.getContent(), data);
            } catch (IOException exception) {
                throw new RuntimeException("Failed to send notification");
            }
        }
        datesCoordinationsRepository.findById(datesCoordinationDto.id()).ifPresent(
            DatesCoordinations::completeAlarm);
    }
}
