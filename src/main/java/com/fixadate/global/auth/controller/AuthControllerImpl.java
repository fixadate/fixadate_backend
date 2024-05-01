package com.fixadate.global.auth.controller;

import com.fixadate.domain.member.entity.Member;
import com.fixadate.global.auth.dto.request.MemberOAuthRequest;
import com.fixadate.global.auth.dto.request.MemberRegistRequest;
import com.fixadate.global.auth.service.AuthService;
import com.fixadate.global.jwt.entity.TokenResponse;
import com.fixadate.global.jwt.service.JwtProvider;
import com.fixadate.global.util.S3Utils;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.fixadate.global.oauth.ConstantValue.ACCESS_TOKEN;
import static com.fixadate.global.oauth.ConstantValue.REFRESH_TOKEN;

@RestController
@RequiredArgsConstructor
public class AuthControllerImpl implements AuthController {
    private final AuthService authService;
    private final S3Utils s3Utils;
    private final JwtProvider jwtProvider;

    @Override
    @PostMapping("/signin")
    public ResponseEntity<Void> signin(@Valid @RequestBody MemberOAuthRequest memberOAuthRequest) {
        Member member = authService.memberSignIn(memberOAuthRequest);

        TokenResponse tokenResponse = jwtProvider.getTokenResponse(member.getId().toString());

        ResponseCookie cookie = authService.createHttpOnlyCooke(tokenResponse.getRefreshToken());

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(HttpHeaders.SET_COOKIE, cookie.toString());
        httpHeaders.add(ACCESS_TOKEN, tokenResponse.getAccessToken());

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

    @Override
    @PostMapping("/reissue")
    public ResponseEntity<Void> reissueAccessToken(
            @CookieValue(value = REFRESH_TOKEN, required = true) Cookie cookie,
            @RequestHeader String accessToken) {
        TokenResponse tokenResponse = jwtProvider.reIssueToken(cookie, accessToken);
        ResponseCookie ncookie = authService.createHttpOnlyCooke(tokenResponse.getRefreshToken());

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(HttpHeaders.SET_COOKIE, ncookie.toString());
        httpHeaders.add(ACCESS_TOKEN, tokenResponse.getAccessToken());

        return ResponseEntity.noContent()
                .headers(httpHeaders)
                .build();
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest httpServletRequest) {
        String accessToken = jwtProvider.retrieveToken(httpServletRequest);
        jwtProvider.memberLogout(accessToken);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/signout")
    public ResponseEntity<Void> signout(@RequestParam String email, HttpServletRequest httpServletRequest) {
        String accessToken = jwtProvider.retrieveToken(httpServletRequest);
        jwtProvider.memberLogout(accessToken);

        authService.memberSignout(email, jwtProvider.getIdFromToken(accessToken));
        return ResponseEntity.noContent().build();
    }
}
