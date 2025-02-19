package com.fixadate.domain.adate.repository;

import com.fixadate.domain.adate.entity.ToDo;
import com.fixadate.domain.adate.service.repository.ToDoRepository;
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
}
