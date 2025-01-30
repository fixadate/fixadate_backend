package com.fixadate.domain.dates.entity;

import com.fixadate.domain.auth.entity.BaseEntity;
import com.fixadate.domain.member.entity.Member;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.codehaus.jackson.annotate.JsonBackReference;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "team_members")
public class TeamMembers extends BaseEntity {
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

    @Column(nullable = false, unique = true)
    @Enumerated(EnumType.STRING)
    private Grades grades;

    @Column
    private String updatedBy; // 누가 team_member의 grade를 변경했는지

    public enum Grades{
        OWNER, MANAGER, MEMBER
    }

    @Builder
    public TeamMembers(Teams team, Member member, Grades grades) {
        this.team = team;
        this.member = member;
        this.grades = grades;
    }
}