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

    @Column(unique = true)
    protected String calendarId;

    protected String title;

    @Column(length = 500)
    protected String notes;

    protected LocalDateTime startsWhen;

    protected LocalDateTime endsWhen;

    protected String etag; // 동기화 등을 위해 필요하다면 유지

    // 생성자
    public Calendar(String calendarId, String title, String notes, LocalDateTime startsWhen, LocalDateTime endsWhen) {
        this.calendarId = calendarId;
        this.title = title;
        this.notes = notes;
        this.startsWhen = startsWhen;
        this.endsWhen = endsWhen;
    }

    public void updateTitle(final String title) {
        this.title = title;
    }

    public void updateNotes(final String notes) {
        this.notes = notes;
    }

    public void updateStartsWhen(final LocalDateTime startsWhen) {
        this.startsWhen = startsWhen;
    }

    public void updateEndsWhen(final LocalDateTime endsWhen) {
        this.endsWhen = endsWhen;
    }

}