package com.fixadate.domain.member.repository.fixture;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;

import com.fixadate.config.FixtureMonkeyConfig;
import com.fixadate.domain.auth.entity.OAuthProvider;
import com.fixadate.domain.member.entity.Member;
import com.fixadate.domain.member.repository.MemberJpaRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import com.navercorp.fixturemonkey.FixtureMonkey;

@SuppressWarnings("NonAsciiCharacters")
public class MemberRepositoryFixture {

	@PersistenceContext
	private EntityManager em;

	@Autowired
	protected MemberJpaRepository memberJpaRepository;

	protected Member 멤버;
	protected Member 저장된_멤버;
	private final FixtureMonkey fixtureMonkey = FixtureMonkeyConfig.entityMonkey();

	@BeforeEach
	void setUp() {
		멤버 = fixtureMonkey.giveMeBuilder(Member.class)
						  .setNotNull("oauthId")
						  .setNotNull("name")
						  .setNotNull("email")
						  .set("oauthPlatform", OAuthProvider.GOOGLE)
						  .setNotNull("signatureColor")
						  .setNotNull("nickname")
						  .setNull("googleCredentials")
						  .setNull("pushKey")
						  .sample();

		저장된_멤버 = fixtureMonkey.giveMeBuilder(Member.class)
							  .setNotNull("oauthId")
							  .setNotNull("email")
							  .set("oauthPlatform", OAuthProvider.GOOGLE)
							  .setNotNull("name")
							  .setNotNull("signatureColor")
							  .setNotNull("nickname")
							  .setNull("googleCredentials")
							  .setNull("pushKey")
							  .sample();
		memberJpaRepository.save(저장된_멤버);

		em.flush();
		em.clear();
	}
}
