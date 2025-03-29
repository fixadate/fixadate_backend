package com.fixadate.domain.dates.dto;

public record CalendarDto(
    String title,
    String startsWhen,
    String endsWhen,
    boolean isAdate
){
}
