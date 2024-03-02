package com.fixadate.global.auth.service;


import com.fixadate.domain.member.entity.Member;
import com.fixadate.domain.member.exception.MemberNotFoundException;
import com.fixadate.domain.member.repository.MemberRepository;
import com.fixadate.global.auth.dto.request.MemberRegistRequestDto;

import java.time.Duration;
import java.util.Optional;

import com.fixadate.global.auth.exception.MemberSigninException;
import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import static com.fixadate.global.oauth.ConstantValue.REFRESH_TOKEN;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final MemberRepository memberRepository;

    public Member findMemberByOAuthId(String oauthId) {
        return memberRepository.findMemberByOauthId(oauthId).orElseThrow(MemberSigninException::new);
    }

    @Transactional
    public void registMember(MemberRegistRequestDto memberRegistRequestDto) {
        Member member = memberRegistRequestDto.of();
        memberRepository.save(member);
    }

    public Cookie createHttpOnlyCooke(String token) {
        Cookie cookie = new Cookie(REFRESH_TOKEN, token);
        cookie.setSecure(true);
        cookie.setHttpOnly(true);
        return cookie;
    }
}
