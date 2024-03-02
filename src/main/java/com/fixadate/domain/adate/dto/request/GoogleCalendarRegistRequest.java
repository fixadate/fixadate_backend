package com.fixadate.domain.adate.dto.request;

import com.fixadate.domain.adate.entity.Adate;
import com.fixadate.domain.member.entity.Member;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record GoogleCalendarRegistRequest(
        @NotBlank(message = "adate title cannot be blank") String title,
        String notes,
        String location,
        Boolean ifAllDay,
        LocalDateTime startsWhen,
        LocalDateTime endsWhen,
        LocalDateTime alertWhen,
        LocalDateTime repeatFreq,
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
