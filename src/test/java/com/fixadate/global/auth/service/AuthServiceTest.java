//package com.fixadate.global.auth.service;
//
//import com.fixadate.domain.member.entity.Member;
//import com.fixadate.domain.member.repository.MemberRepository;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
//import org.springframework.boot.test.context.SpringBootTest;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//
//@SpringBootTest
//class AuthServiceTest {
//
//    @Autowired
//    private AuthService authService;
//
//    @Autowired
//    private MemberRepository memberRepository;
//
//    @DisplayName("member가 존재할 때 test")
//    @Test
//    void findMemberByOauthIdIfMemberExists() {
//        // given
//        String oauthId = "213123";
//        Member member = Member.builder()
//                .oauthId(oauthId)
//                .build();
//        memberRepository.save(member);
//
//        // when
//        Member foundMember = authService.findMemberByOAuthId(oauthId);
//
//        // then
//        assertEquals(member.getOauthId(), foundMember.getOauthId());
//    }
//}
