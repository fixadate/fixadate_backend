package com.fixadate.domain.adate.dto.request;

import com.fixadate.domain.member.entity.Member;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

public record TodoRegisterRequest(
    @NotBlank(message = "ToDo title cannot be blank")
    String title,
    LocalDate date
) {
}
