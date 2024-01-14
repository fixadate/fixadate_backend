package com.fixadate.domain.adate.dto.request;

import com.fixadate.domain.adate.entity.Adate;
import com.fixadate.domain.member.entity.Member;
import jakarta.persistence.Column;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

public class NewAdateRequest {
    public static Adate toEntity(Adate adate, GoogleCalendarRegistRequest googleCalendarRegistRequest) {
        return Adate.builder().
                id(adate.getId())
                .title(googleCalendarRegistRequest.title())
                .notes(googleCalendarRegistRequest.notes())
                .location(googleCalendarRegistRequest.location())
                .alertWhen(googleCalendarRegistRequest.alertWhen())
                .repeatFreq(googleCalendarRegistRequest.repeatFreq())
                .color(googleCalendarRegistRequest.color())
                .adateName(googleCalendarRegistRequest.adateName())
                .ifAllDay(googleCalendarRegistRequest.ifAllDay())
                .startsWhen(googleCalendarRegistRequest.startsWhen())
                .endsWhen(googleCalendarRegistRequest.endsWhen())
                .calendarId(googleCalendarRegistRequest.calendarId())
                .reminders(googleCalendarRegistRequest.reminders())
                .version(googleCalendarRegistRequest.version())
                .created(googleCalendarRegistRequest.created())
                .recurringEventId(googleCalendarRegistRequest.recurringEventId())
                .status(googleCalendarRegistRequest.status())
                .member(adate.getMember())
                .build();
    }
}
