package com.fixadate.domain.member.entity;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "plans")
public class Plan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private PlanType name;

    @Column(nullable = false)
    private Integer resourceLimit;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "plan_permissions", joinColumns = @JoinColumn(name = "plan_id"))
    @Column(name = "permission")
    private Set<String> permissions = new HashSet<>();

    public enum PlanType {
        FREE,
        BASIC,
        STANDARD,
        PREMIUM,
        ENTERPRISE
    }

}
