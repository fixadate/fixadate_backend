package com.fixadate.domain.adate.repository;

import com.fixadate.domain.adate.entity.ToDo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ToDoJpaRepository extends JpaRepository<ToDo, Long> {
}
