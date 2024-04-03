package com.fixadate.domain.adate.dto.response;

import com.fixadate.domain.adate.entity.Adate;

import java.time.LocalDateTime;

public record AdateCalendarEventResponse(
        String title,
        String notes,
        String location,
        LocalDateTime alertWhen,
        LocalDateTime repeatFreq,
        String color,
        String adateName,
        boolean ifAllDay,
        LocalDateTime startsWhen,
        LocalDateTime endsWhen,
        String calendarId,
        boolean reminders,
        String status
) {

    public static AdateCalendarEventResponse of(Adate adate) {
        return new AdateCalendarEventResponse(
                adate.getTitle(),
                adate.getNotes(),
                adate.getLocation(),
                adate.getAlertWhen(),
                adate.getRepeatFreq(),
                adate.getColor(),
                adate.getAdateName(),
                adate.getIfAllDay(),
                adate.getStartsWhen(),
                adate.getEndsWhen(),
                adate.getCalendarId(),
                adate.isReminders(),
                adate.getStatus()
        );
    }
}
