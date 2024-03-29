package com.fixadate.domain.adate.entity;

import com.fixadate.domain.colortype.entity.ColorType;
import com.fixadate.domain.member.entity.Member;
import com.fixadate.global.auth.entity.BaseTimeEntity;
import com.google.api.services.calendar.model.Event;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import static com.fixadate.domain.adate.dto.response.GoogleCalendarEventResponse.convertDateTimeToLocalDateTime;

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
    private String etag;
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

    public void updateFrom(Event event) {
        this.title = event.getSummary();
        this.notes = event.getDescription();
        this.location = event.getLocation();
        this.color = event.getColorId();
        this.startsWhen = convertDateTimeToLocalDateTime(event.getStart().getDateTime());
        this.endsWhen = convertDateTimeToLocalDateTime(event.getEnd().getDateTime());
        this.calendarId = event.getId();
        this.etag = event.getEtag();
        this.reminders = event.getReminders().getUseDefault();
        this.recurringEventId = event.getRecurringEventId();
        this.status = event.getStatus();
    }

    public static Adate getAdateFromEvent(Event event, Member member) {
        return Adate.builder()
                .title(event.getSummary())
                .notes(event.getDescription())
                .location(event.getLocation())
                .color(event.getColorId())
                .startsWhen(convertDateTimeToLocalDateTime(event.getStart().getDateTime()))
                .endsWhen(convertDateTimeToLocalDateTime(event.getEnd().getDateTime()))
                .calendarId(event.getId())
                .etag(event.getEtag())
                .reminders(event.getReminders().getUseDefault())
                .recurringEventId(event.getRecurringEventId())
                .status(event.getStatus())
                .member(member)
                .build();
    }
    public void setColorType(ColorType colorType) {
        this.colorType = colorType;
    }
}
