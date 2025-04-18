package com.fixadate.domain.notification.entity;

import com.fixadate.domain.auth.entity.BaseEntity;

import com.fixadate.domain.member.entity.Member;
import com.fixadate.domain.notification.dto.NotificationListResponse.ValueObject;
import com.fixadate.domain.notification.enumerations.PushNotificationType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
public class Notification extends BaseEntity {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @Column(nullable = false)
    private String memberId;
    @Column(nullable = false)
    private String pushKey;

    private String image;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PushNotificationType eventType;

    @Column(nullable = false)
    private String value;

    @Column(nullable = false)
    private boolean isRead;

    private boolean isReSend;

    @Builder
    public Notification(Member member, PushNotificationType eventType, String title, String content, String value, String image, String pushKey) {
        this.memberId = member.getId();
        this.eventType = eventType;
        this.title = title;
        this.content = content;
        this.value = value;
        this.image = image;
        this.pushKey = pushKey;
        this.isRead = false;
        this.isReSend = false;
    }

    public void reSend() {
        this.isReSend = true;
    }

    public void read() {
        this.isRead = true;
    }

    public String shortenTitle(String title) {
        return title.substring(0, 10) + "...";
    }

    public ValueObject createValueObj(){
        ValueObject valueObject = new ValueObject();
        switch (eventType) {
            case DATES_MARK_REQUEST -> valueObject.setMarkRequestDatesId(value);
            case WORKSPACE_INVITATION -> valueObject.setInvitationId(value);
            case DATES_CHOICE -> valueObject.setChoiceDatesId(value);
            case DATES_CONFIRMED -> valueObject.setConfirmedDatesId(value);
        }
        return valueObject;
    }
}
