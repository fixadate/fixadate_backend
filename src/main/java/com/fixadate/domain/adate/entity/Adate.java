package com.fixadate.domain.adate.entity;

import com.fixadate.domain.adate.dto.request.GoogleCalendarRegistRequest;
import com.fixadate.domain.colortype.entity.ColorType;
import com.fixadate.domain.member.entity.Member;
import com.fixadate.global.auth.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Table(indexes = @Index(name = "calendar_id", columnList = "calendarId", unique = true))
public class Adate extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    @Column(length = 500)
    private String notes;
    @Column(length = 300)
    private String location;

    private LocalDateTime alertWhen;
    private LocalDateTime repeatFreq;
    private String color;
    private String adateName;
    private Boolean ifAllDay;
    private LocalDateTime startsWhen;
    private LocalDateTime endsWhen;
    @Column(unique = true)
    private String calendarId;
    private boolean reminders;
    private LocalDateTime version;
    private LocalDateTime created;
    private String recurringEventId;
    private String status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "colorType_id")
    private ColorType colorType;

    public void updateFrom(GoogleCalendarRegistRequest other) {
        this.title = other.title();
        this.notes = other.notes();
        this.location = other.location();
        this.alertWhen = other.alertWhen();
        this.repeatFreq = other.repeatFreq();
        this.color = other.color();
        this.adateName = other.adateName();
        this.ifAllDay = other.ifAllDay();
        this.startsWhen = other.startsWhen();
        this.endsWhen = other.endsWhen();
        this.calendarId = other.calendarId();
        this.reminders = other.reminders();
        this.recurringEventId = other.recurringEventId();
        this.status = other.status();
    }
    public void setColorType(ColorType colorType) {
        this.colorType = colorType;
    }
}
