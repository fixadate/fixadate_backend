package com.fixadate.domain.member.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.fixadate.domain.auth.entity.OAuthProvider;
import com.fixadate.domain.member.entity.Member;

public interface MemberJpaRepository extends JpaRepository<Member, Long> {

	Optional<Member> findMemberById(final String id);

	Optional<Member> findMemberByEmail(final String email);

	@Query("""
			SELECT m
			FROM Member m 
			WHERE m.oauthPlatform = :oauthPlatform AND m.email = :email AND m.name = :memberName
		""")
	Optional<Member> findMemberByOauthPlatformAndEmailAndName(
		@Param("oauthPlatform") final OAuthProvider oauthPlatform,
		@Param("email") final String email,
		@Param("memberName") final String memberName
	);
}
