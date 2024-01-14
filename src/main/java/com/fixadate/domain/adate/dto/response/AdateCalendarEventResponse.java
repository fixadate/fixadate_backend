package com.fixadate.domain.adate.dto.response;

import com.fixadate.domain.adate.entity.Adate;

import java.time.LocalDateTime;
import java.util.Date;

public record AdateCalendarEventResponse(
        String title,
        String notes,
        String location,
        Date alertWhen,
        Date repeatFreq,
        String color,
        String adateName,
        boolean ifAllDay,
        LocalDateTime startsWhen,
        LocalDateTime endsWhen,
        String calendarId,
        boolean reminders,
        String recurringEventId,
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
                adate.getRecurringEventId(),
                adate.getStatus()
        );
    }
}
