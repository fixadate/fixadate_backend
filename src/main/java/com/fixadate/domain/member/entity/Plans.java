package com.fixadate.domain.member.entity;

import com.fixadate.domain.auth.entity.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.HashSet;
import java.util.Set;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.codehaus.jackson.annotate.JsonManagedReference;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "plans")
public class Plans extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "plan_id")
    private Long id;

    @Column(nullable = false, unique = true)
    @Enumerated(EnumType.STRING)
    private PlanType name;

    @Column(nullable = false, unique = true)
    private Integer price;

    @JsonManagedReference
    @OneToMany(mappedBy = "plan")
    private Set<PlanPermissions> planPermissions = new HashSet<>();

    @JsonManagedReference
    @OneToMany(mappedBy = "plan")
    private Set<PlanResources> planResources = new HashSet<>();

    @Column
    private String updatedBy;

    public enum PlanType {
        FREE,
        BASIC,
        STANDARD,
        PREMIUM,
        ENTERPRISE
    }

}
