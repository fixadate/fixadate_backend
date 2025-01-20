package com.fixadate.domain.member.entity;

import com.fixadate.domain.auth.entity.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "member_plans")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemberPlans extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne
    @JoinColumn(name = "plan_id")
    private Plans plan;

    @Column(name = "begin_at")
    private LocalDateTime beginAt;

    @Column(name = "end_at")
    private LocalDateTime endAt;

    @Column(name = "valid_yn")
    private boolean validYn;
}
