package com.fixadate.domain.member.service.fixture;

import java.io.File;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import net.jqwik.api.Arbitraries;

import com.fixadate.config.FixtureMonkeyConfig;
import com.fixadate.domain.member.dto.MemberInfoUpdateDto;
import com.fixadate.domain.member.entity.Member;
import com.fixadate.domain.member.service.repository.MemberRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import com.navercorp.fixturemonkey.FixtureMonkey;
import com.navercorp.fixturemonkey.api.arbitrary.CombinableArbitrary;
import com.navercorp.fixturemonkey.customizer.Values;

@SuppressWarnings("NonAsciiCharacters")
public class MemberServiceFixture {

	private static final FixtureMonkey 엔티티_몽키 = FixtureMonkeyConfig.entityMonkey();
	private static final FixtureMonkey 전달_객체_몽키 = FixtureMonkeyConfig.jakartaValidationMonkey();

	@PersistenceContext
	private EntityManager em;

	@Autowired
	private MemberRepository memberRepository;

	@Value("${cloud.aws.bucket-name}")
	private String bucket;

	protected PutObjectRequest 입력_요청;
	protected RequestBody 입력_바디;
	protected Member 멤버;
	protected String 멤버_아이디;
	protected MemberInfoUpdateDto 멤버_정보_수정_요청;
	protected MemberInfoUpdateDto 멤버_정보_수정_요청_이미지_없는_경우;

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

		멤버_정보_수정_요청 = 전달_객체_몽키.giveMeBuilder(MemberInfoUpdateDto.class)
							  .set("memberId", 멤버_아이디)
							  .set("profileImg", Values.just(CombinableArbitrary.from(
								  () -> Arbitraries.strings().alpha().ofMinLength(5).sample())
							  ))
							  .setNotNull("nickname")
							  .setNotNull("signatureColor")
							  .setNotNull("profession")
							  .sample();

		멤버_정보_수정_요청_이미지_없는_경우 = 전달_객체_몽키.giveMeBuilder(MemberInfoUpdateDto.class)
										.set("memberId", 멤버_아이디)
										.setNull("profileImg")
										.setNotNull("nickname")
										.setNotNull("signatureColor")
										.setNotNull("profession")
										.sample();


		입력_요청 = PutObjectRequest.builder()
								.bucket(bucket)
								.key(멤버.getProfileImg())
								.build();

		File file = new File("src/test/resources/application.yml");
		입력_바디 = RequestBody.fromFile(file);

		em.flush();
		em.clear();
	}
}
