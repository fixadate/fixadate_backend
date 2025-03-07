package com.fixadate.domain.member.entity;

import com.fixadate.domain.auth.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Builder
@EntityListeners(AuditingEntityListener.class)
@Entity
@Table(name = "member_plans")
@NoArgsConstructor
@AllArgsConstructor
public class MemberPlans extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @Getter
    @ManyToOne
    @JoinColumn(name = "plan_id")
    private Plans plan;

    @Column(name = "begin_at")
    private LocalDateTime beginAt;

    @Column(name = "end_at")
    private LocalDateTime endAt;

    @Column(name = "valid_yn")
    @Builder.Default
    private boolean validYn = true;

    public boolean isValid(){
        return validYn;
    }

}
