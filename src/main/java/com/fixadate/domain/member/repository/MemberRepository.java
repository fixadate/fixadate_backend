package com.fixadate.domain.member.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.fixadate.domain.auth.entity.OAuthProvider;
import com.fixadate.domain.member.entity.Member;

public interface MemberRepository extends JpaRepository<Member, Long> {
	Optional<Member> findMemberById(String id);

	Optional<Member> findMemberByEmail(String email);

	@Query("SELECT m FROM Member m WHERE m.oauthPlatform = :oauthPlatform "
		+ "AND m.email = :email AND m.name = :memberName")
	Optional<Member> findMemberByOauthPlatformAndEmailAndName(@Param("oauthPlatform") OAuthProvider oauthPlatform,
		@Param("email") String email,
		@Param("memberName") String memberName);
}
