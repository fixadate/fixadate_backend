package com.fixadate.domain.member.entity;

import com.fixadate.domain.auth.entity.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "resources")
public class Resources extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "resource_id")
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column
    private int cnt;

    @Column
    private String updatedBy;
}
