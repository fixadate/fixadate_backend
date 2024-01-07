package com.fixadate.domain.adate.dto.response;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Event.Reminders;
import com.google.api.services.calendar.model.EventDateTime;
import jakarta.persistence.Column;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GoogleCalendarEventResponse {
    @Column(nullable = false)
    private LocalDateTime created;
    @Column(nullable = false)
    private LocalDateTime end;
    @Column(nullable = false)
    private String id;
    private Reminders reminders;
    @Column(nullable = false)
    private LocalDateTime start;
    private String location;
    private String summary;
    private String description;
    @Column(nullable = false)
    private LocalDateTime version;
    private String recurringEventId;

    public static GoogleCalendarEventResponse of(Event event) {
        return GoogleCalendarEventResponse.builder()
                .created(getLocalDateTimeFromDateTime(event.getCreated()))
                .end(getLocalDateTimeFromEventDateTime(event.getEnd()))
                .id(event.getId())
                .reminders(event.getReminders())
                .start(getLocalDateTimeFromEventDateTime(event.getStart()))
                .location(event.getLocation())
                .summary(event.getSummary())
                .description(event.getDescription())
                .version(getLocalDateTimeFromDateTime(event.getUpdated()))
                .recurringEventId(event.getRecurringEventId())
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

    private static LocalDateTime getLocalDateTimeFromEventDateTime(EventDateTime eventDateTime) {
        long eventTimeMilliseconds = eventDateTime.getDateTime().getValue();
        Instant instant = Instant.ofEpochMilli(eventTimeMilliseconds);
        return LocalDateTime.ofInstant(instant, ZoneId.of("Asia/Seoul"));
    }
}
