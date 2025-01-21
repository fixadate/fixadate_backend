package com.fixadate.domain.dates.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import java.util.HashSet;

@Entity
@Table(name = "team_permissions")
public class TeamPermissions {
    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "permission_name")
    private String permissionName;

    @Column(name = "http_method")
    private String httpMethod;

    @Column
    private String updatedBy;
}
