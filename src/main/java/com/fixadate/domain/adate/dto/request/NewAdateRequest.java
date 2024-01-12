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
                .title(googleCalendarRegistRequest.getTitle())
                .notes(googleCalendarRegistRequest.getNotes())
                .location(googleCalendarRegistRequest.getLocation())
                .alertWhen(googleCalendarRegistRequest.getAlertWhen())
                .repeatFreq(googleCalendarRegistRequest.getRepeatFreq())
                .color(googleCalendarRegistRequest.getColor())
                .adateName(googleCalendarRegistRequest.getAdateName())
                .ifAllDay(googleCalendarRegistRequest.getIfAllDay())
                .startsWhen(googleCalendarRegistRequest.getStartsWhen())
                .endsWhen(googleCalendarRegistRequest.getEndsWhen())
                .startDate(googleCalendarRegistRequest.getStartDate())
                .endDate(googleCalendarRegistRequest.getEndDate())
                .calendarId(googleCalendarRegistRequest.getCalendarId())
                .reminders(googleCalendarRegistRequest.isReminders())
                .version(googleCalendarRegistRequest.getVersion())
                .created(googleCalendarRegistRequest.getCreated())
                .recurringEventId(googleCalendarRegistRequest.getRecurringEventId())
                .status(googleCalendarRegistRequest.getStatus())
                .member(adate.getMember())
                .build();
    }
}
