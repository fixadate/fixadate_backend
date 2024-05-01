package com.fixadate.global.auth.controller;

import com.fixadate.domain.member.entity.Member;
import com.fixadate.global.auth.dto.request.MemberOAuthRequest;
import com.fixadate.global.auth.dto.request.MemberRegistRequest;
import com.fixadate.global.auth.service.AuthService;
import com.fixadate.global.jwt.service.JwtProvider;
import com.fixadate.global.util.S3Utils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static com.fixadate.global.oauth.ConstantValue.ACCESS_TOKEN;

@RestController
@RequiredArgsConstructor
public class AuthControllerImpl implements AuthController{
    private final AuthService authService;
    private final S3Utils s3Utils;
    private final JwtProvider jwtProvider;

    @Override
    @PostMapping("/signin")
    public ResponseEntity<Void> signin(@Valid @RequestBody MemberOAuthRequest memberOAuthRequest) {
        Member member = authService.signIn(memberOAuthRequest);

        String accessToken = jwtProvider.createAccessToken(member.getId().toString());
        String refreshToken = jwtProvider.createRefreshToken(member.getId().toString());

        ResponseCookie cookie = authService.createHttpOnlyCooke(refreshToken);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(HttpHeaders.SET_COOKIE, cookie.toString());
        httpHeaders.add(ACCESS_TOKEN, accessToken);

        return ResponseEntity.noContent()
                .headers(httpHeaders)
                .build();
    }

    @Override
    @PostMapping("/signup")
    public ResponseEntity<String> signup(
            @Valid @RequestBody MemberRegistRequest memberRegistRequest) {
        authService.registMember(memberRegistRequest);

        String url = s3Utils.generatePresignedUrlForUpload(memberRegistRequest.profileImg(),
                memberRegistRequest.contentType());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(url);
    }
}
