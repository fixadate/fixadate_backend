package com.fixadate.domain.dates.entity;

import com.fixadate.domain.auth.entity.BaseEntity;
import com.fixadate.domain.member.entity.Member;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.codehaus.jackson.annotate.JsonBackReference;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;


@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "dates_coordination_members")
public class DatesCoordinationMembers extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dates_coordinations_id", foreignKey = @ForeignKey(name = "fk_dates_coordination_members_coordinations_id"))
    private DatesCoordinations datesCoordinations;

    @ManyToOne
    @JoinColumn(name = "team_member_id", foreignKey = @ForeignKey(name = "fk_dates_coordination_members_team_member_id"))
    private TeamMembers member;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Grades grades;

    protected String startsWhen;    // yyyyMMddHHmm
    protected String endsWhen;      // yyyyMMddHHmm

    @Builder
    private DatesCoordinationMembers(
            final DatesCoordinations datesCoordinations,
            final TeamMembers member,
            final Grades grades
    ) {
        this.datesCoordinations = datesCoordinations;
        this.member = member;
        this.grades = grades;
    }

    public void choiceDates(String startsWhen, String endsWhen) {
        this.startsWhen = startsWhen;
        this.endsWhen = endsWhen;
    }

    public boolean isChosen(){
        return startsWhen != null && endsWhen != null;
    }
}