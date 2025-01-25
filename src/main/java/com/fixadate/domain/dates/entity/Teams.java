package com.fixadate.domain.dates.entity;

import com.fixadate.domain.auth.entity.BaseTimeEntity;
import com.fixadate.domain.member.entity.PlanPermissions;
import com.fixadate.domain.member.entity.PlanResources;
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
import lombok.Getter;
import lombok.Setter;
import org.codehaus.jackson.annotate.JsonManagedReference;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@Entity
@Table(name = "teams")
public class Teams extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "team_id")
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column
    private String description;

    @JsonManagedReference
    @OneToMany(mappedBy = "team")
    private Set<TeamMembers> teamMembers = new HashSet<>();

    @Column
    private String updatedBy;

}
