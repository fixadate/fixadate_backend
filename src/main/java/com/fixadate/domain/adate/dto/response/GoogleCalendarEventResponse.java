package com.fixadate.domain.adate.dto.response;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Event.Reminders;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

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
    static final String TRANSPARENCY = "transparency";

    public static GoogleCalendarEventResponse of(Event event) {
        return new GoogleCalendarEventResponse(
                convertDateTimeToLocalDateTime(event.getCreated()),
                convertDateTimeToLocalDateTime(event.getEnd().getDateTime()),
                event.getId(),
                getRemindersDefaultValue(event.getReminders()),
                convertDateTimeToLocalDateTime(event.getStart().getDateTime()),
                event.getLocation(),
                event.getSummary(),
                event.getDescription(),
                convertDateTimeToLocalDateTime(event.getUpdated()),
                event.getRecurringEventId(),
                getIfAllDayFromGetTransparency(event.getTransparency()),
                event.getStatus()
        );
    }

    public static LocalDateTime convertDateTimeToLocalDateTime(DateTime dateTime) {
        OffsetDateTime offsetDateTime = OffsetDateTime.parse(dateTime.toString());
        return offsetDateTime.toLocalDateTime();
    }

    private static boolean getIfAllDayFromGetTransparency(String transparency) {
        return transparency != null && transparency.equals(TRANSPARENCY);
    }

    private static boolean getRemindersDefaultValue(Reminders reminders) {
        return reminders.getUseDefault();
    }
}
