package com.fixadate.domain.member.controller;

import com.fixadate.domain.member.dto.request.AdateColorNameRequestDto;
import com.fixadate.domain.member.entity.Member;
import com.fixadate.domain.member.service.MemberService;
import com.fixadate.global.jwt.MemberPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<String> registAdateColorAndName(
            @RequestBody @Validated AdateColorNameRequestDto adateColorNameRequestDto,
            @AuthenticationPrincipal MemberPrincipal memberPrincipal) {

        Member member = memberService.getMemberWithAdateColorTypes(memberPrincipal.getMember().getId());
        memberService.saveAdateColorAndName(adateColorNameRequestDto, member);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body("color 등록 완료");
    }
}
