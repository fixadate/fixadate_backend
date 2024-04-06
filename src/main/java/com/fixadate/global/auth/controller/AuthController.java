package com.fixadate.global.auth.controller;

import com.fixadate.global.auth.dto.request.MemberOAuthRequestDto;
import com.fixadate.global.auth.dto.request.MemberRegistRequestDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;


@Tag(name = "AuthController", description = "AuthController 입니다.")
public interface AuthController {

    @Operation(summary = "회원 등록", description = "OAuth 인증을 통해 회원을 등록합니다.")
    ResponseEntity<Void> registMember(@Valid MemberOAuthRequestDto memberOAuthRequestDto);

    @Operation(summary = "추가 회원 등록", description = "추가 정보를 포함하여 회원을 등록합니다.")
    ResponseEntity<String> AdditionalRegistMember(@Valid MemberRegistRequestDto memberRegistRequestDto);
}
