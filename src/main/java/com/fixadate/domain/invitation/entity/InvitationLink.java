package com.fixadate.domain.invitation.entity;

import com.fixadate.domain.auth.entity.BaseTimeEntity;
import com.fixadate.domain.dates.entity.Teams;
import com.fixadate.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "invitation_links")
@Getter
public class InvitationLink extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", nullable = false)
    private Teams team;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member creator;

    @Column(nullable = false)
    private String creatorName;

    @Column(nullable = false, unique = true, length = 50)
    private String inviteCode;

    @Column(nullable = false)
    private Boolean active = true;

    @Column(nullable = false)
    private int remainSeat;

    private LocalDateTime expiresAt = LocalDateTime.now().plusDays(4);

    public boolean isExpired() {
        return expiresAt != null && LocalDateTime.now().isAfter(expiresAt);
    }

    public boolean isActive() {
        return active && remainSeat > 0;
    }

    public void decreaseRemainCnt(){
        this.remainSeat -= 1;
        if(this.remainSeat < 0){
            throw new RuntimeException("cannot exceed remainSeat");
        }
        if(this.remainSeat == 0){
            deactivate();
        }
    }

    public void deactivate(){
        this.active = false;
    }

    @Builder
    public InvitationLink(Teams team, Member creator, String inviteCode, int remainSeat) {
        this.team = team;
        this.creator = creator;
        this.inviteCode = inviteCode;
        this.remainSeat = remainSeat;
        this.creatorName = creator.getName();
    }
}
