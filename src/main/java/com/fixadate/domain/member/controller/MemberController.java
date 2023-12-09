package com.fixadate.domain.member.controller;

import com.fixadate.domain.member.dto.request.AdateColorNameRequestDto;
import com.fixadate.domain.member.service.MemberService;
import com.fixadate.global.jwt.MemberPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

    @PostMapping("/member/regist/color")
    @Transactional
    public ResponseEntity<String> registAdateColorAndName(
            @RequestBody @Validated AdateColorNameRequestDto adateColorNameRequestDto,
            @AuthenticationPrincipal MemberPrincipal memberPrincipal) {
        memberService.saveAdateColorAndName(adateColorNameRequestDto, memberPrincipal.getMember());
        return ResponseEntity.ok("color&name 등록 완료");
    }
}
