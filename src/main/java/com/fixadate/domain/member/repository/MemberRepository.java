package com.fixadate.domain.member.repository;

import com.fixadate.domain.member.entity.Member;
import com.fixadate.global.oauth.entity.OAuthProvider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findMemberById(String id);

    Optional<Member> findMemberByEmail(String email);

    @Query("SELECT m FROM Member m WHERE m.oauthPlatform = :oauthPlatform AND m.email = :email AND m.name = :memberName")
    Optional<Member> findMemberByOauthPlatformAndEmailAndName(OAuthProvider oauthPlatform, String email, String memberName);
}
