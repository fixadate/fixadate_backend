package com.fixadate.domain.adate.dto.request;

import com.fixadate.domain.adate.entity.Adate;
import com.fixadate.domain.member.entity.Member;
import jakarta.persistence.Column;

import java.time.LocalDateTime;
import java.util.Date;

public record GoogleCalendarRegistRequest(
        @Column(nullable = false) String title,
        @Column(nullable = false, length = 500) String notes,
        @Column(length = 300) String location,
        Boolean ifAllDay,
        LocalDateTime startsWhen,
        LocalDateTime endsWhen,
        Date alertWhen,
        Date repeatFreq,
        String color,
        String adateName,
        @Column(unique = true) String calendarId,
        boolean reminders,
        LocalDateTime version,
        LocalDateTime created,
        String recurringEventId,
        String status
) {
    public Adate toEntity(Member member) {
        return Adate.builder()
                .title(title)
                .notes(notes)
                .location(location)
                .ifAllDay(ifAllDay)
                .startsWhen(startsWhen)
                .endsWhen(endsWhen)
                .alertWhen(alertWhen)
                .repeatFreq(repeatFreq)
                .color(color)
                .adateName(adateName)
                .calendarId(calendarId)
                .reminders(reminders)
                .version(version)
                .created(created)
                .recurringEventId(recurringEventId)
                .status(status)
                .member(member)
                .build();
    }
}
