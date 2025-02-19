package com.fixadate.domain.adate.service.repository;

import com.fixadate.domain.adate.entity.ToDo;

import java.util.Optional;

public interface ToDoRepository {
    ToDo save(final ToDo toDo);
    Optional<ToDo> findToDoByToDoId(final Long ToDoId);
    ToDo delete(final ToDo toDo);
}
