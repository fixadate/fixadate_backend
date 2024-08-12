package com.fixadate.domain.member.repository.fixture;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;

import com.fixadate.config.FixtureMonkeyConfig;
import com.fixadate.domain.member.entity.Member;
import com.fixadate.domain.member.repository.MemberRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import com.navercorp.fixturemonkey.FixtureMonkey;

@SuppressWarnings("NonAsciiCharacters")
public class MemberRepositoryFixture {

	@PersistenceContext
	private EntityManager em;

	@Autowired
	protected MemberRepository memberRepository;

	protected Member 멤버;
	protected Member 저장된_멤버;
	private final FixtureMonkey fixtureMonkey = FixtureMonkeyConfig.entityMonkey();

	@BeforeEach
	void setUp() {
		멤버 = fixtureMonkey.giveMeBuilder(Member.class)
						  .setNotNull("oauthId")
						  .setNotNull("name")
						  .setNotNull("email")
						  .setNotNull("oauthPlatform")
						  .setNotNull("signatureColor")
						  .setNotNull("nickname")
						  .setNull("googleCredentials")
						  .setNull("pushKey")
						  .sample();

		저장된_멤버 = fixtureMonkey.giveMeBuilder(Member.class)
							  .setNotNull("oauthId")
							  .setNotNull("email")
							  .setNotNull("oauthPlatform")
							  .setNotNull("name")
							  .setNotNull("signatureColor")
							  .setNotNull("nickname")
							  .setNull("googleCredentials")
							  .setNull("pushKey")
							  .sample();
		memberRepository.save(저장된_멤버);

		em.flush();
		em.clear();
	}
}
