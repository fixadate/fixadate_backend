package com.fixadate.domain.member.entity;

import com.fixadate.domain.auth.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@EntityListeners(AuditingEntityListener.class)
@Entity
@Table(name = "member_resources")
public class MemberResources extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_resource_id")
    private Long id;

    @OneToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(nullable = false)
    private int adateCnt;

    @Column(nullable = false)
    private int teamCnt;

    @Column(nullable = false)
    private int datesCnt;

    public int getResourceCnt(ResourceType resourceType){
        switch (resourceType) {
            case ADATE -> { return adateCnt; }
            case TEAM -> { return teamCnt; }
            case DATES -> { return datesCnt; }
            default -> { return 0; }
        }
    };

    public void plusResources(ResourceType resourceType, int cnt){
        switch (resourceType) {
            case ADATE -> adateCnt += cnt;
            case TEAM -> teamCnt += cnt;
            case DATES -> datesCnt += cnt;
        }
    };
    public void minusResources(ResourceType resourceType, int cnt){
        switch (resourceType) {
            case ADATE -> adateCnt -= cnt;
            case TEAM -> teamCnt -= cnt;
            case DATES -> datesCnt -= cnt;
        }
    };
}
