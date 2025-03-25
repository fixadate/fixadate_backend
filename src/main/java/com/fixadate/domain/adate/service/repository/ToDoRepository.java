package com.fixadate.domain.adate.service.repository;

import com.fixadate.domain.adate.entity.ToDo;

import com.fixadate.domain.auth.entity.BaseEntity.DataStatus;
import com.fixadate.domain.member.entity.Member;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ToDoRepository {
    ToDo save(final ToDo toDo);
    Optional<ToDo> findToDoByToDoId(final Long ToDoId);
    ToDo delete(final ToDo toDo);
    List<ToDo> findByMemberAndBetweenDates(Member member, LocalDate firstDayDate, LocalDate lastDayDate);
}
