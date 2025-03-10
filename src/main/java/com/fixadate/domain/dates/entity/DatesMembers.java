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
@Table(name = "dates_members")
public class DatesMembers extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "dates_id")
    private Dates dates;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @Column
    private String updatedBy;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Grades grades;

    @Builder
    private DatesMembers(
            final Dates dates,
            final Member member,
            final Grades grades,
            final String updatedBy
    ) {
        this.dates = dates;
        this.member = member;
        this.grades = grades;
        this.updatedBy = updatedBy;
    }
}