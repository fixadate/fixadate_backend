package com.fixadate.domain.dates.entity;

import com.fixadate.domain.auth.entity.BaseTimeEntity;
import com.fixadate.domain.member.entity.Member;
import com.fixadate.domain.member.entity.Permissions;
import com.fixadate.domain.member.entity.Plans;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import org.codehaus.jackson.annotate.JsonBackReference;
<<<<<<< Updated upstream
=======
import org.joda.time.DateTime;
import org.joda.time.LocalDateTime;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
>>>>>>> Stashed changes

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

<<<<<<< Updated upstream
    @Column
    private String updatedBy;
=======
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Grades grades;

    @Column
    private String updatedBy;

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

>>>>>>> Stashed changes
}