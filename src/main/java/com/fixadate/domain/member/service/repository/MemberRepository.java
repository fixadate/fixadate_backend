package com.fixadate.domain.member.service.repository;

import io.lettuce.core.dynamic.annotation.Param;
import java.util.List;
import java.util.Optional;

import com.fixadate.domain.auth.entity.OAuthProvider;
import com.fixadate.domain.member.entity.Member;
import org.springframework.data.jpa.repository.Query;

public interface MemberRepository {

	Optional<Member> findMemberById(final String id);

	Optional<Member> findMemberByEmail(final String email);

	Optional<Member> findMemberByOauthPlatformAndEmailAndName(
		final OAuthProvider oauthPlatform,
		final String email,
		final String memberName
	);

	Member save(final Member member);

	void delete(final Member member);

	List<Member> findMembersByEmailContainingAndExcludingIds(
		@Param("email") String email,
		@Param("memberIds") List<String> memberIds);

}
