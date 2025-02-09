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
import jakarta.persistence.Table;
import lombok.Getter;
import org.codehaus.jackson.annotate.JsonBackReference;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@EntityListeners(AuditingEntityListener.class)
@Entity
@Table(name = "plan_resources")
public class PlanResources extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "plans_id")
    private Plans plan;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "resource_id")
    private Resources resource;

    @Column
    private String updatedBy;

    public Resources getResource() {
        return resource;
    }
}