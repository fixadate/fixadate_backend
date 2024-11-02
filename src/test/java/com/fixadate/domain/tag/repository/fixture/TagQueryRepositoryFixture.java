package com.fixadate.domain.tag.repository.fixture;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;

import com.fixadate.config.FixtureMonkeyConfig;
import com.fixadate.domain.auth.entity.OAuthProvider;
import com.fixadate.domain.member.entity.Member;
import com.fixadate.domain.member.repository.MemberJpaRepository;
import com.fixadate.domain.tag.entity.Tag;
import com.fixadate.domain.tag.repository.TagJpaRepository;

import jakarta.persistence.EntityManager;

import com.navercorp.fixturemonkey.FixtureMonkey;

@SuppressWarnings("NonAsciiCharacters")
public class TagQueryRepositoryFixture {

	@Autowired
	protected EntityManager em;

	@Autowired
	protected TagJpaRepository tagRepository;

	@Autowired
	protected MemberJpaRepository memberRepository;

	protected Member 저장된_회원;
	protected Tag 저장된_태그;
	private final FixtureMonkey fixtureMonkey = FixtureMonkeyConfig.entityMonkey();

	@BeforeEach
	void setUp() {
		저장된_회원 = fixtureMonkey.giveMeBuilder(Member.class)
							  .setNotNull("oauthId")
							  .setNotNull("name")
							  .setNotNull("email")
							  .set("oauthPlatform", OAuthProvider.GOOGLE)
							  .setNotNull("signatureColor")
							  .setNotNull("nickname")
							  .setNull("googleCredentials")
							  .setNull("pushKey")
							  .sample();

		memberRepository.save(저장된_회원);
		em.flush();

		저장된_태그 = fixtureMonkey.giveMeBuilder(Tag.class)
							  .setNotNull("name")
							  .setNotNull("color")
							  .setNotNull("systemDefined")
							  .setNull("adates")
							  .set("member", 저장된_회원)
							  .sample();

		tagRepository.save(저장된_태그);
	}
}
