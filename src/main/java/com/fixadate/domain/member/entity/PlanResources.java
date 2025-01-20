package com.fixadate.domain.member.entity;

import com.fixadate.domain.auth.entity.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "plan_resources")
public class PlanResources extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "plans_id")
    private Plans plan;

    @ManyToOne
    @JoinColumn(name = "resource_id")
    private Resources resource;

    @Column
    private String updatedBy;
}