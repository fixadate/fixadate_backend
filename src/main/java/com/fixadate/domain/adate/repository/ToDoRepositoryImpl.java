package com.fixadate.domain.adate.repository;

import com.fixadate.domain.adate.entity.ToDo;
import com.fixadate.domain.adate.service.repository.ToDoRepository;
import com.fixadate.domain.auth.entity.BaseEntity.DataStatus;
import com.fixadate.domain.member.entity.Member;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ToDoRepositoryImpl implements ToDoRepository {
    private final ToDoJpaRepository toDoJpaRepository;

    @Override
    public ToDo save(final ToDo toDo) {
        return toDoJpaRepository.save(toDo);
    }

    @Override
    public Optional<ToDo> findToDoByToDoId(Long toDoId) {
        return toDoJpaRepository.findById(toDoId);
    }

    @Override
    public ToDo delete(ToDo toDo) {
        toDoJpaRepository.delete(toDo);
        return toDo;
    }

    @Override
    public List<ToDo> findByMemberAndBetweenDates(Member member, LocalDate firstDayDate,
        LocalDate lastDayDateTime) {
        return toDoJpaRepository.findByMemberAndDateBetweenAndStatusIs(member, firstDayDate, lastDayDateTime,
            DataStatus.ACTIVE);
    }
}
