package com.fixadate.domain.tag.service.fixture;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;

import net.jqwik.api.Arbitraries;

import com.fixadate.config.FixtureMonkeyConfig;
import com.fixadate.domain.member.entity.Member;
import com.fixadate.domain.member.service.repository.MemberRepository;
import com.fixadate.domain.tag.dto.TagRegisterDto;
import com.fixadate.domain.tag.dto.TagUpdateDto;
import com.fixadate.domain.tag.dto.request.TagRequest;
import com.fixadate.domain.tag.dto.request.TagUpdateRequest;
import com.fixadate.domain.tag.entity.Tag;
import com.fixadate.domain.tag.service.repository.TagRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import com.navercorp.fixturemonkey.FixtureMonkey;
import com.navercorp.fixturemonkey.api.arbitrary.CombinableArbitrary;
import com.navercorp.fixturemonkey.customizer.Values;

@SuppressWarnings("NonAsciiCharacters")
public class TagServiceFixture {

	private static final FixtureMonkey 엔티티_몽키 = FixtureMonkeyConfig.entityMonkey();
	private static final FixtureMonkey 전달_객체_몽키 = FixtureMonkeyConfig.jakartaValidationMonkey();

	@PersistenceContext
	private EntityManager em;

	@Autowired
	private TagRepository tagRepository;

	@Autowired
	private MemberRepository memberRepository;

	protected Tag 저장된_태그;
	protected Tag 기본_저장된_태그;
	protected Member 저장된_회원;
	protected Member 저장된_회원2;
	protected String 기존_이름;
	protected String 기존_색상;
	protected TagUpdateDto 태그_수정_요청;
	protected TagUpdateDto 기본_태그_수정_요청;
	protected TagUpdateDto 이름이_중복된_태그_수정_요청;
	protected TagRegisterDto 태그_등록_요청;
	protected TagRegisterDto 이미_저장된_태그_등록_요청;
	protected TagUpdateRequest 태그_수정_요청_객체;
	protected TagUpdateRequest 기본_태그_수정_요청_객체;
	protected TagUpdateRequest 이름이_중복된_태그_수정_요청_객체;
	protected TagRequest 태그_등록_요청_객체;
	protected TagRequest 이미_저장된_태그_등록_요청_객체;

	@BeforeEach
	void setUp() {

		저장된_회원 = 엔티티_몽키.giveMeBuilder(Member.class)
					   .set("id", Values.just(CombinableArbitrary.from(
						   () -> Arbitraries.strings().alpha().sample()).unique()
					   ))
					   .set("profileImg", Values.just(CombinableArbitrary.from(
						   () -> Arbitraries.strings().alpha().ofMinLength(5).sample())
					   ))
					   .set("oauthId", Values.just(CombinableArbitrary.from(
						   () -> Arbitraries.strings().alpha().ofMinLength(5).sample())
					   ))
					   .setNotNull("name")
					   .setNotNull("email")
					   .setNotNull("oauthPlatform")
					   .setNotNull("signatureColor")
					   .setNotNull("nickname")
					   .setNull("googleCredentials")
					   .setNull("pushKey")
					   .sample();

		저장된_회원2 = 엔티티_몽키.giveMeBuilder(Member.class)
						.set("id", Values.just(CombinableArbitrary.from(
							() -> Arbitraries.strings().all().sample()).unique()
						))
						.set("profileImg", Values.just(CombinableArbitrary.from(
							() -> Arbitraries.strings().all().ofMinLength(50).sample())
						))
						.set("oauthId", Values.just(CombinableArbitrary.from(
							() -> Arbitraries.strings().all().ofMinLength(50).sample())
						))
						.setNotNull("name")
						.setNotNull("email")
						.setNotNull("oauthPlatform")
						.setNotNull("signatureColor")
						.setNotNull("nickname")
						.setNull("googleCredentials")
						.setNull("pushKey")
						.sample();

		memberRepository.save(저장된_회원);
		memberRepository.save(저장된_회원2);

		em.flush();

		기존_이름 = "oldName123";
		기존_색상 = "oldColor123";

		태그_등록_요청_객체 = 전달_객체_몽키.giveMeBuilder(TagRequest.class)
							  .set("name", 기존_이름)
							  .set("color", 기존_색상)
							  .sample();

		태그_등록_요청 = 전달_객체_몽키.giveMeBuilder(TagRegisterDto.class)
						   .set("tagRequest", 태그_등록_요청_객체)
						   .set("member", 저장된_회원)
						   .sample();

		저장된_태그 = 엔티티_몽키.giveMeBuilder(Tag.class)
					   .set("name", Values.just(CombinableArbitrary.from(
						   () -> Arbitraries.strings().alpha().ofMinLength(100).sample()))
					   )
					   .set("color", Values.just(CombinableArbitrary.from(
						   () -> Arbitraries.strings().alpha().ofMinLength(100).sample()))
					   )
					   .set("systemDefined", false)
					   .set("member", 저장된_회원)
					   .setNull("adates")
					   .sample();

		기본_저장된_태그 = 엔티티_몽키.giveMeBuilder(Tag.class)
						  .set("name", Values.just(CombinableArbitrary.from(
							  () -> Arbitraries.strings().alpha().ofMinLength(100).sample()))
						  )
						  .set("color", Values.just(CombinableArbitrary.from(
							  () -> Arbitraries.strings().alpha().ofMinLength(100).sample()))
						  )
						  .set("systemDefined", true)
						  .set("member", 저장된_회원2)
						  .setNull("adates")
						  .sample();

		tagRepository.save(저장된_태그);
		tagRepository.save(기본_저장된_태그);

		이미_저장된_태그_등록_요청_객체 = 전달_객체_몽키.giveMeBuilder(TagRequest.class)
									 .set("name", 저장된_태그.getName())
									 .set("color", 저장된_태그.getColor())
									 .sample();


		이미_저장된_태그_등록_요청 = 전달_객체_몽키.giveMeBuilder(TagRegisterDto.class)
								  .set("tagRequest", 이미_저장된_태그_등록_요청_객체)
								  .set("member", 저장된_회원)
								  .sample();

		태그_수정_요청_객체 = 전달_객체_몽키.giveMeBuilder(TagUpdateRequest.class)
							  .set("name", 저장된_태그.getName())
							  .set("newName", Values.just(CombinableArbitrary.from(
								  () -> Arbitraries.strings().alpha().ofMinLength(100).sample()))
							  )
							  .set("newColor", Values.just(CombinableArbitrary.from(
								  () -> Arbitraries.strings().alpha().ofMinLength(100).sample()))
							  )
							  .sample();

		태그_수정_요청 = 전달_객체_몽키.giveMeBuilder(TagUpdateDto.class)
						   .set("tagUpdateRequest", 태그_수정_요청_객체)
						   .set("member", 저장된_회원)
						   .sample();

		기본_태그_수정_요청_객체 = 전달_객체_몽키.giveMeBuilder(TagUpdateRequest.class)
								 .set("name", 기본_저장된_태그.getName())
								 .set("newName", Values.just(CombinableArbitrary.from(
									 () -> Arbitraries.strings().alpha().ofMinLength(100).sample()))
								 )
								 .set("newColor", Values.just(CombinableArbitrary.from(
									 () -> Arbitraries.strings().alpha().ofMinLength(100).sample()))
								 )
								 .sample();

		기본_태그_수정_요청 = 전달_객체_몽키.giveMeBuilder(TagUpdateDto.class)
							  .set("tagUpdateRequest", 기본_태그_수정_요청_객체)
							  .set("member", 저장된_회원2)
							  .sample();

		이름이_중복된_태그_수정_요청_객체 = 전달_객체_몽키.giveMeBuilder(TagUpdateRequest.class)
									  .set("name", 저장된_태그.getName())
									  .set("newName", 저장된_태그.getName())
									  .set("newColor", 저장된_태그.getColor())
									  .sample();

		이름이_중복된_태그_수정_요청 = 전달_객체_몽키.giveMeBuilder(TagUpdateDto.class)
								   .set("tagUpdateRequest", 이름이_중복된_태그_수정_요청_객체)
								   .set("member", 저장된_회원)
								   .sample();
	}
}
