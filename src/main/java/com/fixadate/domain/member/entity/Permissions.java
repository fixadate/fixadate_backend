package com.fixadate.domain.member.entity;

import com.fixadate.domain.auth.entity.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "permissions")
public class Permissions extends BaseTimeEntity {
    @Id
    @GeneratedValue
    @Column(name = "permission_id")
    private Long id;

    @Column(name = "permission_name")
    private String permissionName;

    @Column(name = "http_method")
    private String httpMethod;

    @OneToMany(mappedBy = "permission")
    private Set<PlanPermissions> planPermissions = new HashSet<>();

    @Column
    private String updatedBy;
}
