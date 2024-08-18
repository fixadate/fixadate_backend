package com.fixadate.domain.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fixadate.domain.member.entity.Member;

public interface MemberJpaRepository extends JpaRepository<Member, Long> {
}
