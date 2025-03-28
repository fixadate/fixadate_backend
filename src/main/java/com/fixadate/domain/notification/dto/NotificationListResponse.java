package com.fixadate.domain.notification.dto;

import com.fixadate.domain.notification.entity.Notification;
import com.fixadate.domain.notification.enumerations.PushNotificationType;
import com.fixadate.global.util.TimeUtil;
import lombok.Data;

@Data
public class NotificationListResponse {

    private Long notificationId;
    private String memberId;
    private PushNotificationType eventType;
    private String title;
    private String content;
    private ValueObject valueObj;
    private String image;
    private boolean isRead;
    private String createdAt;

    public NotificationListResponse(Notification notification) {
        this.notificationId = notification.getId();
        this.memberId = notification.getMemberId();
        this.eventType = notification.getEventType();
        this.title = notification.getTitle();
        this.content = notification.getContent();
        this.valueObj = notification.createValueObj();
        this.image = notification.getImage();
        this.isRead = notification.isRead();
        this.createdAt = TimeUtil.convertDateToKor(notification.getCreateDate());
    }

    @Data
    public static class ValueObject{
        private String markRequestDatesId;
        private String invitationId;
        private String choiceDatesId;
        private String confirmedDatesId;
    }


}
