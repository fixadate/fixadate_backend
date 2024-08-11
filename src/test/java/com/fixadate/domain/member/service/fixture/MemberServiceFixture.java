package com.fixadate.domain.member.service.fixture;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;

import net.jqwik.api.Arbitraries;

import com.fixadate.domain.member.dto.MemberInfoUpdateDto;
import com.fixadate.domain.member.entity.Member;
import com.fixadate.domain.member.repository.MemberRepository;
import com.fixadate.integration.config.FixtureMonkeyConfig;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import com.navercorp.fixturemonkey.FixtureMonkey;
import com.navercorp.fixturemonkey.api.arbitrary.CombinableArbitrary;
import com.navercorp.fixturemonkey.customizer.Values;

@SuppressWarnings("NonAsciiCharacters")
public class MemberServiceFixture {

	@PersistenceContext
	private EntityManager em;

	@Autowired
	private MemberRepository memberRepository;

	protected Member 멤버;
	protected String 멤버_아이디;
	protected MemberInfoUpdateDto 멤버_정보_수정_요청;
	protected MemberInfoUpdateDto 멤버_정보_수정_요청_이미지_없는_경우;
	private static final FixtureMonkey 엔티티_몽키 = FixtureMonkeyConfig.entityMonkey();
	private static final FixtureMonkey DTO_몽키 = FixtureMonkeyConfig.jakartaValidationMonkey();

	@BeforeEach
	void setUp() {
		멤버 = 엔티티_몽키.giveMeBuilder(Member.class)
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

		멤버_아이디 = 멤버.getId();
		memberRepository.save(멤버);
		em.flush();
		em.clear();

		멤버_정보_수정_요청 = DTO_몽키.giveMeBuilder(MemberInfoUpdateDto.class)
							.set("memberId", 멤버_아이디)
							.setNotNull("profileImg")
							.sample();

		멤버_정보_수정_요청_이미지_없는_경우 = DTO_몽키.giveMeBuilder(MemberInfoUpdateDto.class)
									  .set("memberId", 멤버_아이디)
									  .setNull("profileImg")
									  .sample();
	}
}
