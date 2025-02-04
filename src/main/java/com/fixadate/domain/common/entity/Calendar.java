package com.fixadate.domain.common.entity;

import com.fixadate.domain.auth.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.*;

import java.time.LocalDateTime;

@MappedSuperclass // 테이블 생성 X, 상속받는 엔티티가 컬럼을 포함
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@EqualsAndHashCode(callSuper = false)
@ToString
public abstract class Calendar extends BaseEntity {

    protected String title;

    @Column(length = 500)
    protected String notes;

    @Column(length = 300)
    protected String location;

    protected LocalDateTime alertWhen;

    protected LocalDateTime repeatFreq;

    protected boolean ifAllDay;

    protected LocalDateTime startsWhen;

    protected LocalDateTime endsWhen;

    protected String etag; // 동기화 등을 위해 필요하다면 유지

    // 생성자
    public Calendar(String title, String notes, String location, LocalDateTime startsWhen, LocalDateTime endsWhen) {
        this.title = title;
        this.notes = notes;
        this.location = location;
        this.startsWhen = startsWhen;
        this.endsWhen = endsWhen;
    }

    public void updateTitle(final String title) {
        this.title = title;
    }

    public void updateNotes(final String notes) {
        this.notes = notes;
    }

    public void updateLocation(final String location) {
        this.location = location;
    }

    public void updateAlertWhen(final LocalDateTime alertWhen) {
        this.alertWhen = alertWhen;
    }

    public void updateRepeatFreq(final LocalDateTime repeatFreq) {
        this.repeatFreq = repeatFreq;
    }

    public void updateIfAllDay(final boolean ifAllDay) {
        this.ifAllDay = ifAllDay;
    }

    public void updateStartsWhen(final LocalDateTime startsWhen) {
        this.startsWhen = startsWhen;
    }

    public void updateEndsWhen(final LocalDateTime endsWhen) {
        this.endsWhen = endsWhen;
    }

}