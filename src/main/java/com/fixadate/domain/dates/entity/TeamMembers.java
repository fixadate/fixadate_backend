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
}