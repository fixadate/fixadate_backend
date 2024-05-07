package com.fixadate.domain.adate.dto.request;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record AdateUpdateRequest(
        String title,
        String notes,
        String location,
        LocalDateTime alertWhen,
        LocalDateTime repeatFreq,
        String color,
        String adateName,
        @NotNull boolean ifAllDay,
        LocalDateTime startsWhen,
        LocalDateTime endsWhen,
        @NotNull boolean reminders) {
}
