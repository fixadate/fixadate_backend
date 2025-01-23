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
import lombok.Getter;
import org.codehaus.jackson.annotate.JsonBackReference;

@Getter
@Entity
@Table(name = "plan_permissions")
public class PlanPermissions extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "plan_id")
    private Plans plan;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "permission_id")
    private Permissions permission;

    @Column
    private String updatedBy;
}