package com.fixadate.domain.adate.repository;

import com.fixadate.domain.adate.entity.ToDo;
import com.fixadate.domain.auth.entity.BaseEntity.DataStatus;
import com.fixadate.domain.member.entity.Member;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ToDoJpaRepository extends JpaRepository<ToDo, Long> {
    List<ToDo> findByMemberAndDateBetweenAndStatusIs(Member member, LocalDate localDate, LocalDate endDate, DataStatus dataStatus);
}
