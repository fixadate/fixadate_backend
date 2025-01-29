package com.fixadate.domain.member.repository;

import com.fixadate.domain.member.entity.MemberPlans;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberPlansJpaRepository extends JpaRepository<MemberPlans, Long> {
}
