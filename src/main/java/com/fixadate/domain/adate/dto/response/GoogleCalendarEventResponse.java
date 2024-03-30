package com.fixadate.domain.adate.dto.response;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Event.Reminders;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Builder
@Slf4j
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
    static final String DATE_TIME_FORMATTER = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    static final String TRANSPARENCY = "transparency";

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

    public static LocalDateTime getLocalDateTimeFromDateTime(DateTime dateTime) {
        try {
            DateTimeFormatter f = DateTimeFormatter.ofPattern(DATE_TIME_FORMATTER);
            return LocalDateTime.parse(dateTime.toStringRfc3339(), f);
        } catch (Exception e) {
            //fixme 정보를 알려주는 log로 변경할 것
            e.printStackTrace();
            return null;
        }
    }

    private static boolean getIfAllDayFromGetTransparency(String transparency) {
        return transparency != null && transparency.equals(TRANSPARENCY);
    }

    private static boolean getRemindersDefaultValue(Reminders reminders) {
        return reminders.getUseDefault();
    }
}
