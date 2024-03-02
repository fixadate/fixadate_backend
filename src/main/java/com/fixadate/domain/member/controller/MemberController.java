package com.fixadate.domain.member.controller;

import com.fixadate.domain.member.service.MemberService;

import java.util.List;

import com.fixadate.global.util.S3Utils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/member")
@Slf4j
public class MemberController {
    private final MemberService memberService;
    private final S3Utils s3Utils;

    @Value("${randNick.adjs}")
    private String randomAdjs;
    @Value("${randNick.nouns}")
    private String randomNouns;

    @GetMapping("/nickname")
    public ResponseEntity<String> getRandomNickname() {
        List<String> adjs = List.of(randomAdjs.split(","));
        List<String> nouns = List.of(randomNouns.split(","));

        String adj = memberService.getRandomNickname(adjs);
        String noun = memberService.getRandomNickname(nouns);
        String randomNickname = adj + " " + noun;
        return ResponseEntity.ok(randomNickname);
    }

    @GetMapping("/profile-img")
    public ResponseEntity<String> getProfileImagePresignedUrl(@RequestParam String filename) {
        return ResponseEntity.ok(s3Utils.generatePresignedUrlForDownload(filename));
    }

    @DeleteMapping("/profile-img")
    public ResponseEntity<String> getProfileImageDeletePresignedUrl(@RequestParam String filename) {
        return ResponseEntity.ok(s3Utils.generatePresignedUrlForDelete(filename));
    }
}
