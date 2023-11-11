package com.fixadate.domain.member.repository;

import com.fixadate.domain.member.entity.Member;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findMemberByOauthId(String oauthId);
}
