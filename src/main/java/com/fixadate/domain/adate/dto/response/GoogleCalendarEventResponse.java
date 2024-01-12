package com.fixadate.domain.adate.dto.response;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Event.ExtendedProperties;
import com.google.api.services.calendar.model.Event.Reminders;
import com.google.api.services.calendar.model.EventDateTime;
import jakarta.persistence.Column;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
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
    private LocalDate startDate;
    private LocalDate endDate;
    private String status;

    public static GoogleCalendarEventResponse of(Event event) {
        return GoogleCalendarEventResponse.builder()
                .created(getLocalDateTimeFromDateTime(event.getCreated()))
                .end(getLocalDateTimeFromEventDateTime(event.getEnd()))
                .id(event.getId())
                .reminders(getRemindersDefaultValue(event.getReminders()))
                .start(getLocalDateTimeFromEventDateTime(event.getStart()))
                .location(event.getLocation())
                .summary(event.getSummary())
                .description(event.getDescription())
                .version(getLocalDateTimeFromDateTime(event.getUpdated()))
                .recurringEventId(event.getRecurringEventId())
                .ifAllDay(getIfAllDayFromGetTransparency(event.getTransparency()))
                .startDate(getLocalDateFromEventDateTime(event.getStart()))
                .endDate(getLocalDateFromEventDateTime(event.getEnd()))
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

    private static LocalDateTime getLocalDateTimeFromEventDateTime(EventDateTime eventDateTime) {
        try {
            long eventTimeMilliseconds = eventDateTime.getDateTime().getValue();
            Instant instant = Instant.ofEpochMilli(eventTimeMilliseconds);
            return LocalDateTime.ofInstant(instant, ZoneId.of("Asia/Seoul"));
        } catch (NullPointerException e) {
            return null;
        }
    }

    // FIXME: 2024-01-08 10일부터 11일까지 -> 10 to 12로 보임. 애플 캘린더는 10 to 11이면 구글 캘린더에서 endDate 하루 앞당기기
    private static LocalDate getLocalDateFromEventDateTime(EventDateTime eventDateTime) {
        try {
            DateTime dateTime = eventDateTime.getDate();
            log.info(dateTime.toString());
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate localDate = LocalDate.parse(dateTime.toStringRfc3339(), formatter);
            return localDate;
        } catch (Exception e) {
            return null;
        }
    }

    private static boolean getIfAllDayFromGetTransparency(String transparency) {
        return transparency != null && transparency.equals("transparent");
    }

    private static boolean getRemindersDefaultValue(Reminders reminders) {
        return reminders.getUseDefault();
    }
}
