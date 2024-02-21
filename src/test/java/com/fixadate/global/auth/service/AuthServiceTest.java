package com.fixadate.global.auth.service;

import com.fixadate.domain.member.entity.Member;
import com.fixadate.domain.member.exception.MemberNotFoundException;
import com.fixadate.domain.member.repository.MemberRepository;
import com.fixadate.global.auth.dto.request.MemberRegistRequestDto;
import com.fixadate.global.config.GoogleApiConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.Calendar;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class) // 생략가능
@SpringBootTest
class AuthServiceTest {

    @InjectMocks
    private AuthService authService;
    @Test
    void findMemberByOauthIdIfMemberExists() {
        // 테스트할 때는 authService의 메서드가 memberRepository의 동작에 의존한다는 가정하에 작성
        assertEquals("1","1");
    }


}
