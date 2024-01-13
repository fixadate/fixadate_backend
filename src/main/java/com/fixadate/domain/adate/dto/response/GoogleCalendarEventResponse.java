package com.fixadate.domain.adate.dto.response;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Event.ExtendedProperties;
import com.google.api.services.calendar.model.Event.Reminders;
import com.google.api.services.calendar.model.EventDateTime;
import jakarta.persistence.Column;

import java.time.*;
import java.time.format.DateTimeFormatter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
public class GoogleCalendarEventResponse {
    @Column(nullable = false)
    private LocalDateTime created;
    @Column(nullable = false)
    private LocalDateTime end;
    @Column(nullable = false)
    private String id;
    private boolean reminders;
    @Column(nullable = false)
    private LocalDateTime start;
    private String location;
    private String summary;
    private String description;
    @Column(nullable = false)
    private LocalDateTime version;
    private String recurringEventId;
    private boolean ifAllDay;
    private String status;

    public static GoogleCalendarEventResponse of(Event event) {
        return GoogleCalendarEventResponse.builder()
                .id(event.getId())
                .created(getLocalDateTimeFromDateTime(event.getCreated()))
                .end(getLocalDateTimeFromEventDateTime(event.getEnd(), false))
                .start(getLocalDateTimeFromEventDateTime(event.getStart(), true))
                .reminders(getRemindersDefaultValue(event.getReminders()))
                .location(event.getLocation())
                .summary(event.getSummary())
                .description(event.getDescription())
                .version(getLocalDateTimeFromDateTime(event.getUpdated()))
                .recurringEventId(event.getRecurringEventId())
                .ifAllDay(getIfAllDayFromGetTransparency(event.getTransparency()))
                .status(event.getStatus())
                .build();
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
