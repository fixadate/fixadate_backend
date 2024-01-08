package com.fixadate.domain.adate.dto.request;

import com.fixadate.domain.adate.entity.Adate;
import com.fixadate.domain.member.entity.Member;
import jakarta.persistence.Column;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class GoogleCalendarRegistRequest {
    @Column(nullable = false)
    private String title;
    @Column(nullable = false, length = 500)
    private String notes;
    @Column(length = 300)
    private String location;
    private Boolean ifAllDay;
    private LocalDateTime startsWhen;
    private LocalDateTime endsWhen;
    private LocalDate startDate;
    private LocalDate endDate;
    private Date alertWhen;
    private Date repeatFreq;
    private String color;
    private String adateName;

    /*
    google calendar
     */
    @Column(unique = true)
    private String calendarId;
    private String reminders;
    private LocalDateTime version;
    private LocalDateTime created;
    private String recurringEventId;

    public Adate toEntity(Member member) {
        return Adate.builder()
                .title(title)
                .notes(notes)
                .location(location)
                .ifAllDay(ifAllDay)
                .startsWhen(startsWhen)
                .endsWhen(endsWhen)
                .startDate(startDate)
                .endDate(endDate)
                .alertWhen(alertWhen)
                .repeatFreq(repeatFreq)
                .color(color)
                .adateName(adateName)
                .calendarId(calendarId)
                .reminders(reminders)
                .version(version)
                .created(created)
                .recurringEventId(recurringEventId)
                .member(member)
                .build();
    }
}
