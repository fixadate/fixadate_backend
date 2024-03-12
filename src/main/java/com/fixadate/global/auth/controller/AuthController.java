package com.fixadate.global.auth.controller;

import static com.fixadate.global.oauth.ConstantValue.ACCESS_TOKEN;

import com.fixadate.domain.member.entity.Member;
import com.fixadate.global.auth.dto.request.MemberOAuthRequestDto;
import com.fixadate.global.auth.dto.request.MemberRegistRequestDto;
import com.fixadate.global.auth.service.AuthService;
import com.fixadate.global.jwt.service.JwtProvider;
import com.fixadate.global.util.S3Utils;
import jakarta.servlet.http.Cookie;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final S3Utils s3Utils;
    private final JwtProvider jwtProvider;

    @PostMapping("/member")
    public ResponseEntity<Void> registMember(@Valid @RequestBody MemberOAuthRequestDto memberOAuthRequestDto) {
        String oauthId = memberOAuthRequestDto.oauthId();
        Member member = authService.findMemberByOAuthId(oauthId);
        String accessToken = jwtProvider.createAccessToken(member.getOauthId());
        String refreshToken = jwtProvider.createRefreshToken(member.getOauthId());

        Cookie cookie = authService.createHttpOnlyCooke(refreshToken);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(HttpHeaders.SET_COOKIE, cookie.toString());
        httpHeaders.add(ACCESS_TOKEN, accessToken);

        return ResponseEntity.noContent()
                .headers(httpHeaders)
                .build();
    }

    @PostMapping("/member/additional")
    public ResponseEntity<String> AdditionalRegistMember(
            @Valid @RequestBody MemberRegistRequestDto memberRegistRequestDto) {
        authService.registMember(memberRegistRequestDto);

        String url = s3Utils.generatePresignedUrlForUpload(memberRegistRequestDto.profileImg(),
                memberRegistRequestDto.contentType());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(url);
    }
}
