package com.fixadate.domain.adate.dto.response;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Event.ExtendedProperties;
import com.google.api.services.calendar.model.Event.Reminders;
import com.google.api.services.calendar.model.EventDateTime;
import jakarta.persistence.Column;

import java.time.*;
import java.time.format.DateTimeFormatter;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Builder
@Slf4j
public record GoogleCalendarEventResponse(
        @Column(nullable = false) LocalDateTime created,
        @Column(nullable = false) LocalDateTime end,
        @Column(nullable = false) String id,
        boolean reminders,
        @Column(nullable = false) LocalDateTime start,
        String location,
        String summary,
        String description,
        @Column(nullable = false) LocalDateTime version,
        String recurringEventId,
        boolean ifAllDay,
        String status
) {

    public static GoogleCalendarEventResponse of(Event event) {
        return new GoogleCalendarEventResponse(
                getLocalDateTimeFromDateTime(event.getCreated()),
                getLocalDateTimeFromEventDateTime(event.getEnd(), false),
                event.getId(),
                getRemindersDefaultValue(event.getReminders()),
                getLocalDateTimeFromEventDateTime(event.getStart(), true),
                event.getLocation(),
                event.getSummary(),
                event.getDescription(),
                getLocalDateTimeFromDateTime(event.getUpdated()),
                event.getRecurringEventId(),
                getIfAllDayFromGetTransparency(event.getTransparency()),
                event.getStatus()
        );
    }

    private static LocalDateTime getLocalDateTimeFromDateTime(DateTime dateTime) {
        try {
            DateTimeFormatter f = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            return LocalDateTime.parse(dateTime.toStringRfc3339(), f);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static LocalDateTime getLocalDateTimeFromEventDateTime(EventDateTime eventDateTime, boolean d) {
        if (eventDateTime.getDateTime() != null) {
            long eventTimeMilliseconds = eventDateTime.getDateTime().getValue();
            Instant instant = Instant.ofEpochMilli(eventTimeMilliseconds);
            return LocalDateTime.ofInstant(instant, ZoneId.of("Asia/Seoul"));
        } else {
            LocalDate eventDate = LocalDate.parse(eventDateTime.getDate().toStringRfc3339());
            LocalTime timeToAdd = d ? LocalTime.MIN : LocalTime.MAX;
            return eventDate.atTime(timeToAdd).atZone(ZoneId.of("Asia/Seoul")).toLocalDateTime();
        }
    }

    private static boolean getIfAllDayFromGetTransparency(String transparency) {
        return transparency != null && transparency.equals("transparent");
    }

    private static boolean getRemindersDefaultValue(Reminders reminders) {
        return reminders.getUseDefault();
    }
}
