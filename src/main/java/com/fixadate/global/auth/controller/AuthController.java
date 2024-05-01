package com.fixadate.global.auth.controller;

import com.fixadate.global.auth.dto.request.MemberOAuthRequest;
import com.fixadate.global.auth.dto.request.MemberRegistRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;


@Tag(name = "AuthController", description = "AuthController 입니다.")
public interface AuthController {

    @Operation(summary = "로그인", description = "OAuth 대조를 통해 로그인을 합니다.")
    @RequestBody(description = "member 확인을 위한 oauthId", content = @Content(schema = @Schema(implementation = MemberOAuthRequest.class)))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그인 성공",
                    content = @Content(schema = @Schema(implementation = Void.class)),
                    headers = {
                            @Header(name = "cookie", description = "refreshToken이 담겨있는 httpOnlyCookie입니다."),
                            @Header(name = "accessToken", description = "accessToken입니다.")
                    }
            ),
            @ApiResponse(responseCode = "401", description = "로그인 실패",
                    content = @Content(schema = @Schema(implementation = Void.class)))
    })
    ResponseEntity<Void> signin(@Valid MemberOAuthRequest memberOAuthRequest);

    @Operation(summary = "추가 회원 등록", description = "추가 정보를 포함하여 회원을 등록합니다.")
    @RequestBody(description = "회원가입", content = @Content(schema = @Schema(implementation = MemberRegistRequest.class)))
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "회원 가입 성공, profile 이미지 등록을 위해 링크 발급",
                    content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "400", description = "이미 member가 데이터베이스에 존재할 때",
            content = @Content(schema = @Schema(implementation = Void.class)))

    })
    ResponseEntity<String> signup(@Valid MemberRegistRequest memberRegistRequest);
}
