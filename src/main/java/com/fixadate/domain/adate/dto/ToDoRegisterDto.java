package com.fixadate.domain.adate.dto;

import com.fixadate.domain.member.entity.Member;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

public record ToDoRegisterDto (
        String title,
        LocalDate date,
        Member member
) {
}
