package com.fixadate.global.auth.controller;

import static com.fixadate.global.oauth.ConstantValue.ACCESS_TOKEN;
import static com.fixadate.global.oauth.ConstantValue.REFRESH_TOKEN;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.fixadate.domain.member.entity.Member;
import com.fixadate.global.auth.dto.request.MemberOAuthRequestDto;
import com.fixadate.global.auth.dto.request.MemberRegistRequestDto;
import com.fixadate.global.auth.exception.MemberSigninException;
import com.fixadate.global.auth.service.AuthService;
import com.fixadate.global.jwt.service.JwtProvider;
import jakarta.servlet.http.Cookie;

import java.net.URL;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import software.amazon.awssdk.services.s3.S3Client;

@RestController
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final JwtProvider jwtProvider;

    @PostMapping("/auth/member")
    public ResponseEntity<Void> registMember(@RequestBody @Validated MemberOAuthRequestDto memberOAuthRequestDto) {
        String oauthId = memberOAuthRequestDto.oauthId();
        Optional<Member> memberOptional = authService.findMemberByOAuthId(oauthId);
        if (memberOptional.isEmpty()) {
            throw new MemberSigninException();
        }
        String accessToken = jwtProvider.createAccessToken(oauthId);
        String refreshToken = jwtProvider.createRefreshToken(oauthId);

        Cookie cookie = createHttpOnlyCookie(refreshToken);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(HttpHeaders.SET_COOKIE, cookie.toString());
        httpHeaders.add(ACCESS_TOKEN, accessToken);

        return ResponseEntity.noContent()
                .headers(httpHeaders)
                .build();
    }

    @Transactional
    @PostMapping("/auth/member/additional")
    public ResponseEntity<String> AdditionalRegistMember(
            @RequestBody @Validated MemberRegistRequestDto memberRegistRequestDto) {
        authService.registMember(memberRegistRequestDto);

        String url = authService.getPresignedUrlFromRequest(memberRegistRequestDto);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(url);
    }

    private Cookie createHttpOnlyCookie(String refreshToken) {
        Cookie cookie = new Cookie(REFRESH_TOKEN, refreshToken);
        cookie.setSecure(true);
        cookie.setHttpOnly(true);
        return cookie;
    }
}
