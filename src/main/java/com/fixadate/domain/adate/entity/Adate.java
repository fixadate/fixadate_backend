package com.fixadate.domain.adate.entity;

import com.fixadate.domain.member.entity.Member;
import com.fixadate.global.auth.entity.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
/*
calendarId를 통해 조회를 한 뒤, version을 비교하므로 자주 조회를 하고, 동일한 값이 없는 calendarId를 index로 정했다.
 */
@Table(indexes = @Index(name = "calendar_id", columnList = "calendarId", unique = true))
public class Adate extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private String title;
    @Column(length = 500)
    private String notes;
    @Column(length = 300)
    private String location;
    private Boolean ifAllDay;
    private Date startsWhen;
    private Date endsWhen;
    private Date alertWhen;
    private Date repeatFreq;
    private String color;
    private String adateName;

    /*
    google calendar
     */
    @Column(unique = true)
    private String calendarId;
    private String reminders;
    private LocalDateTime version;
    private LocalDateTime created;
    private String recurringEventId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;
}
