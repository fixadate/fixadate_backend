package com.fixadate.global.auth.service;

import com.fixadate.domain.member.entity.Member;
import com.fixadate.domain.member.repository.MemberRepository;
import com.fixadate.global.auth.dto.request.MemberRegistRequestDto;
import com.fixadate.global.auth.exception.MemberSigninException;
import com.fixadate.global.auth.exception.UnknownOAuthPlatformException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;

@SpringBootTest
class AuthServiceTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private MemberRepository memberRepository;

    @Nested
    @DisplayName("oauthId 검색 테스트")
    class findMemberByOauthIdTest {
        @Test
        @DisplayName("Member의 oauthId가 존재하는 경우")
        void findMemberByOauthIdIfMemberExistsTest() {
            Member member = Member.builder()
                    .oauthId("123")
                    .name("kevin")
                    .nickname("hong")
                    .signatureColor("red")
                    .build();
            memberRepository.save(member);
            Member findMember = authService.findMemberByOAuthId("123");
            Assertions.assertEquals("kevin", findMember.getName());
        }

        @Test
        @DisplayName("Member의 oauthId가 존재하지 않는 경우 예외 발생")
        void findMemberByOauthIdIfMemberNotExistTest() {
            Member member = Member.builder()
                    .oauthId("1234")
                    .name("kevin")
                    .nickname("hong")
                    .signatureColor("red")
                    .build();
            memberRepository.save(member);

            Assertions.assertThrows(MemberSigninException.class, () -> authService.findMemberByOAuthId("1"));
        }
    }

    @Nested
    @DisplayName("Member 저장 테스트")
    class registMemberTest {
        @Test
        @DisplayName("동일한 oauthId를 가진 Member가 존재하는 경우 에러 발생")
        void registMemberTestIfDuplicatedIdExist() {
            Member member = Member.builder()
                    .id(1L)
                    .oauthId("12345")
                    .name("kevin")
                    .nickname("hong")
                    .signatureColor("red")
                    .build();
            memberRepository.save(member);

            MemberRegistRequestDto memberRegistRequestDto =
                    new MemberRegistRequestDto("12345", "kakao", "yongjun", "213", "kevin",
                            20000928, "male", "student", "red", "img");
            Assertions.assertThrows(DataIntegrityViolationException.class, () -> authService.registMember(memberRegistRequestDto));
        }

        @Test
        @DisplayName("잘못된 oauthPlatform 값이 들어간 경우 에러 발생")
        void registMemberTestIfoauthPlatformHasInvalidValue() {
            MemberRegistRequestDto memberRegistRequestDto =
                    new MemberRegistRequestDto("12345", "daum", "yongjun", "213", "kevin",
                            20000928, "male", "student", "red", "img");
            Assertions.assertThrows(UnknownOAuthPlatformException.class, () -> authService.registMember(memberRegistRequestDto));
        }

        @Test
        @DisplayName("저장이 정상적으로 되는 경우")
        void registMemberTestIfEveryThingsIsOk() {
            MemberRegistRequestDto memberRegistRequestDto =
                    new MemberRegistRequestDto("12345", "google", "yongjun", "213", "kevin",
                            20000928, "male", "student", "red", "img");
            Assertions.assertDoesNotThrow(() -> authService.registMember(memberRegistRequestDto));
            Member member = authService.findMemberByOAuthId("12345");
            Assertions.assertEquals("yongjun",member.getName());
        }
    }
}