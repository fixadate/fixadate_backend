package com.fixadate.domain.adate.entity;

import com.fixadate.domain.auth.entity.BaseEntity;
import com.fixadate.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDate;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ToDo extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private ToDoStatus toDoStatus;
    private LocalDate date;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", foreignKey = @ForeignKey(name = "fk_todo_member_id"))
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Member member;

    @Builder
    private ToDo(
        final String title,
        final ToDoStatus toDoStatus,
        final LocalDate date,
        final Member member
    ) {
      this.title = title;
      this.toDoStatus = toDoStatus;
      this.date = date;
      this.member = member;
    }

    public void updateToDoStatus(final ToDoStatus toDoStatus) {
        this.toDoStatus = toDoStatus;
    }

    public void updateTitle(final String title) {
        this.title = title;
    }
}
