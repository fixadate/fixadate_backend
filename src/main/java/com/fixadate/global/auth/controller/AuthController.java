package com.fixadate.global.auth.controller;

import static com.fixadate.global.oauth.ConstantValue.ACCESS_TOKEN;
import static com.fixadate.global.oauth.ConstantValue.REFRESH_TOKEN;

import com.fixadate.domain.member.entity.Member;
import com.fixadate.global.auth.dto.request.MemberOAuthRequestDto;
import com.fixadate.global.auth.dto.request.MemberRegistRequestDto;
import com.fixadate.global.auth.exception.MemberSigninException;
import com.fixadate.global.auth.service.AuthService;
import com.fixadate.global.jwt.service.JwtProvider;
import jakarta.servlet.http.Cookie;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final JwtProvider jwtProvider;
    @PostMapping("/auth/member")
    public ResponseEntity<?> registMember(@RequestBody @Validated MemberOAuthRequestDto memberOAuthRequestDto) {
        String oauthId = memberOAuthRequestDto.getOauthId();
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
        return new ResponseEntity<>(httpHeaders, HttpStatus.OK);
    }

    @PostMapping("/auth/additional/member")
    public ResponseEntity<?> AdditionalRegistMember(
            @RequestBody @Validated MemberRegistRequestDto memberRegistRequestDto) {
        authService.registMember(memberRegistRequestDto);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body("member 등록 완료");
    }

    private Cookie createHttpOnlyCookie(String refreshToken) {
        Cookie cookie = new Cookie(REFRESH_TOKEN, refreshToken);
        cookie.setSecure(true);
        cookie.setHttpOnly(true);
        return cookie;
    }
}
