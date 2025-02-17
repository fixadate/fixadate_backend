package com.fixadate.domain.member.repository;

import com.fixadate.domain.member.entity.Member;
import com.fixadate.domain.member.entity.MemberResources;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberResourcesJpaRepository extends JpaRepository<MemberResources, Long> {
    Optional<MemberResources> findByMember(Member member);
}
