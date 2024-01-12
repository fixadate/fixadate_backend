package com.fixadate.domain.adate.dto.response;

import com.fixadate.domain.adate.entity.Adate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AdateCalendarEventResponse {
    private String title;
    private String notes;
    private String location;
    private Date alertWhen;
    private Date repeatFreq;
    private String color;
    private String adateName;
    private boolean ifAllDay;
    private LocalDateTime startsWhen;
    private LocalDateTime endsWhen;
    private String calendarId;
    private boolean reminders;
    private String recurringEventId;
    private String status;

    public static AdateCalendarEventResponse of(Adate adate) {
        return AdateCalendarEventResponse.builder()
                .title(adate.getTitle())
                .notes(adate.getNotes())
                .location(adate.getLocation())
                .alertWhen(adate.getAlertWhen())
                .repeatFreq(adate.getRepeatFreq())
                .color(adate.getColor())
                .adateName(adate.getAdateName())
                .ifAllDay(adate.getIfAllDay())
                .startsWhen(adate.getStartsWhen())
                .endsWhen(adate.getEndsWhen())
                .calendarId(adate.getCalendarId())
                .reminders(adate.isReminders())
                .recurringEventId(adate.getRecurringEventId())
                .status(adate.getStatus())
                .build();
    }
}
