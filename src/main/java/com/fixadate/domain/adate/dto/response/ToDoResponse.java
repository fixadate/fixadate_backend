package com.fixadate.domain.adate.dto.response;

import com.fixadate.domain.adate.entity.ToDoStatus;
import com.fixadate.domain.member.dto.response.MemberInfoResponse;
import com.fixadate.domain.member.entity.Member;

import java.time.LocalDate;

public record ToDoResponse (
    String title,
    ToDoStatus toDoStatus,
    LocalDate date,
    MemberInfoResponse memberInfoResponse
) {
}
