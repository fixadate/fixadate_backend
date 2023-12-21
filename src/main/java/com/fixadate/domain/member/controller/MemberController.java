package com.fixadate.domain.member.controller;

import com.fixadate.domain.member.dto.request.AdateColorNameRequestDto;
import com.fixadate.domain.member.entity.Member;
import com.fixadate.domain.member.service.MemberService;
import com.fixadate.global.jwt.MemberPrincipal;
import java.util.List;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Value;
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
@Slf4j
public class MemberController {
    private final MemberService memberService;

    @Value("${randNick.adjs}")
    private String randomAdjs;
    @Value("${randNick.nouns}")
    private String randomNouns;

    @PostMapping("/member/regist/color")
    public ResponseEntity<Void> registAdateColorAndName(
            @RequestBody @Validated AdateColorNameRequestDto adateColorNameRequestDto,
            @AuthenticationPrincipal MemberPrincipal memberPrincipal) {

        Member member = memberService.getMemberWithAdateColorTypes(memberPrincipal.getMember().getId());
        memberService.saveAdateColorAndName(adateColorNameRequestDto, member);
        return ResponseEntity.status(HttpStatus.CREATED)
                .build();
    }

    @PostMapping("/member/random/nickname")
    public ResponseEntity<String> getRandomNickname() {
        List<String> adjs = List.of(randomAdjs.split(","));
        List<String> nouns = List.of(randomNouns.split(","));

        String adj = memberService.getRandomNickname(adjs);
        String noun = memberService.getRandomNickname(nouns);
        String randomNickname = adj + " " + noun;
        return ResponseEntity.ok(randomNickname);
    }

}
