package com.fixadate.domain.adate.dto;

import com.fixadate.domain.adate.entity.ToDoStatus;
import com.fixadate.domain.member.entity.Member;

import java.time.LocalDate;

public record ToDoDto (
        Long id,
        String title,
        ToDoStatus status,
        LocalDate date,
        Member member
) {
}
