package com.fixadate.domain.dates.entity;

import com.fixadate.domain.auth.entity.BaseTimeEntity;
import com.fixadate.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import org.codehaus.jackson.annotate.JsonBackReference;


@Getter
@Entity
@Table(name = "team_members")
public class TeamMembers extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "team_id")
    private Teams team;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;


    @Column
    private String updatedBy;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Grades grades;



    @Builder
    private TeamMembers(
            final Teams team,
            final Member member,
            final Grades grades,
            final String updatedBy
    ) {
        this.team = team;
        this.member = member;
        this.grades = grades;
        this.updatedBy = updatedBy;
    }

    public TeamMembers() {

    }
}