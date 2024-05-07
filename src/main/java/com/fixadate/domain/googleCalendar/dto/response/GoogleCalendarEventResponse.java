package com.fixadate.domain.googleCalendar.dto.response;

import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Event.Reminders;

import java.time.LocalDateTime;

import static com.fixadate.global.util.DateTimeParseUtils.getLocalDateTimeFromDateTime;
import static com.fixadate.global.util.constant.ConstantValue.TRANSPARENCY;

public record GoogleCalendarEventResponse(
        LocalDateTime created,
        LocalDateTime end,
        String id,
        boolean reminders,
        LocalDateTime start,
        String location,
        String summary,
        String description,
        LocalDateTime version,
        String recurringEventId,
        boolean ifAllDay,
        String status
) {

    public static GoogleCalendarEventResponse of(Event event) {
        return new GoogleCalendarEventResponse(
                getLocalDateTimeFromDateTime(event.getCreated()),
                getLocalDateTimeFromDateTime(event.getEnd().getDateTime()),
                event.getId(),
                getRemindersDefaultValue(event.getReminders()),
                getLocalDateTimeFromDateTime(event.getStart().getDateTime()),
                event.getLocation(),
                event.getSummary(),
                event.getDescription(),
                getLocalDateTimeFromDateTime(event.getUpdated()),
                event.getRecurringEventId(),
                getIfAllDayFromGetTransparency(event.getTransparency()),
                event.getStatus()
        );
    }
    private static boolean getIfAllDayFromGetTransparency(String transparency) {
        return transparency != null && transparency.equals(TRANSPARENCY.getValue());
    }
    private static boolean getRemindersDefaultValue(Reminders reminders) {
        return reminders.getUseDefault();
    }
}
