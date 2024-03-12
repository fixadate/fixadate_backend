package com.fixadate.domain.member.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

@Tag(name = "MemberController", description = "MemberController 입니다.")
public interface MemberController {

    @Operation(summary = "랜덤 닉네임 생성", description = "랜덤한 닉네임을 생성합니다.")
    ResponseEntity<String> getRandomNickname();

    @Operation(summary = "프로필 이미지 Presigned URL 조회", description = "프로필 이미지의 Presigned URL을 조회합니다.")
    ResponseEntity<String> getProfileImagePresignedUrl(String filename);

    @Operation(summary = "프로필 이미지 Presigned URL 삭제", description = "프로필 이미지의 Presigned URL을 삭제합니다.")
    ResponseEntity<String> getProfileImageDeletePresignedUrl(String filename);
}
