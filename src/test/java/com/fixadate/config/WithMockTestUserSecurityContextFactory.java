package com.fixadate.config;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import net.jqwik.api.Arbitraries;

import com.fixadate.config.annotation.WithMockTestUser;
import com.fixadate.domain.member.entity.Member;
import com.fixadate.global.jwt.MemberPrincipal;

import com.navercorp.fixturemonkey.FixtureMonkey;
import com.navercorp.fixturemonkey.api.arbitrary.CombinableArbitrary;
import com.navercorp.fixturemonkey.customizer.Values;

public class WithMockTestUserSecurityContextFactory implements WithSecurityContextFactory<WithMockTestUser> {

	private static final FixtureMonkey fixtureMonkey = FixtureMonkeyConfig.entityMonkey();

	@Override
	public SecurityContext createSecurityContext(final WithMockTestUser annotation) {
		SecurityContext context = SecurityContextHolder.createEmptyContext();

		Member member = createMember(annotation);
		MemberPrincipal memberPrincipal = new MemberPrincipal(member);

		Authentication auth = new UsernamePasswordAuthenticationToken(
			memberPrincipal,
			"token",
			memberPrincipal.getAuthorities()
		);
		context.setAuthentication(auth);

		return context;
	}

	private Member createMember(WithMockTestUser annotation) {
		if (annotation.autoGenerateMember()) {
			return fixtureMonkey.giveMeBuilder(Member.class)
								.set("id", Values.just(CombinableArbitrary.from(
									() -> Arbitraries.strings().alpha().sample()).unique()
								))
								.set("profileImg", Values.just(CombinableArbitrary.from(
									() -> Arbitraries.strings().alpha().ofMinLength(5).sample())
								))
								.setNotNull("oauthId")
								.setNotNull("name")
								.setNotNull("email")
								.setNotNull("oauthPlatform")
								.setNotNull("signatureColor")
								.setNotNull("nickname")
								.setNull("googleCredentials")
								.setNull("pushKey")
								.sample();
		}

		return fixtureMonkey.giveMeBuilder(Member.class)
							.set("id", annotation.id())
							.set("oauthId", annotation.oauthId())
							.set("name", annotation.name())
							.set("nickname", annotation.nickname())
							.set("birth", annotation.birth())
							.set("gender", annotation.gender())
							.set("profileImg", annotation.profileImg())
							.set("profession", annotation.profession())
							.set("signatureColor", annotation.signatureColor())
							.set("email", annotation.email())
							.set("role", annotation.role())
							.setNull("googleCredentials")
							.setNull("pushKey")
							.sample();
	}
}
