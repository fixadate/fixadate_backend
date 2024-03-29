package com.fixadate.domain.member.repository;

import com.fixadate.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findMemberByOauthId(String oauthId);

    Optional<Member> findMemberById(Long memberId);
}
