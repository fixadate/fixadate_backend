package com.fixadate.domain.googleCalendar.dto.request;

import com.fixadate.domain.adate.entity.Adate;
import com.fixadate.domain.member.entity.Member;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record GoogleCalendarRegistRequest(
        @NotBlank String title,
        String notes,
        String location,
        @NotNull boolean ifAllDay,
        LocalDateTime startsWhen,
        LocalDateTime endsWhen,
        LocalDateTime alertWhen,
        LocalDateTime repeatFreq,
        String color,
        String adateName,
        @NotEmpty String calendarId,
        @NotNull boolean reminders,
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
                .status(status)
                .member(member)
                .build();
    }
}
